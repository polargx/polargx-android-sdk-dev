package com.app.main.di

import android.content.Context
import android.content.Intent
import android.util.Log
import com.app.shared.AppConstants
import com.data.shared.ApiError
import com.app.shared.logger.DebugLogger
import com.data.users.UserRepository
import com.data.users.UserRepositoryImpl
import com.data.users.local.UserLocalDatasource
import com.data.users.local.UserLocalDatasourceImpl
import com.data.users.remote.UserRemoteDatasource
import com.data.users.remote.UserRemoteDatasourceImpl
import com.feature.auth.AuthenticationActivityViewModel
import com.feature.auth.ui.landing.LandingFragmentViewModel
import com.feature.auth.ui.login.LoginFragmentViewModel
import com.feature.auth.ui.sign_up.SignUpFragmentViewModel
import com.library.core.application.BaseApplication
import com.app.main.application.MyApplication
import com.app.main.application.MyApplicationContract
import com.app.main.application.MyApplicationPresenter
import com.app.main.splash.SplashActivityViewModel
import com.linkattribution.sample.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.plugin
import io.ktor.client.request.headers
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.URLProtocol
import io.ktor.http.append
import io.ktor.http.encodedPath
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {

    single { BuildConfig.API_URL }
    single { androidApplication() as? MyApplicationContract.View }
    single { androidApplication() as? BaseApplication }
    single { androidApplication() as? MyApplication }
    single { androidApplication().getSharedPreferences("android_base1.file", Context.MODE_PRIVATE) }

    singleOf(::UserLocalDatasourceImpl) { bind<UserLocalDatasource>() }
    singleOf(::UserRemoteDatasourceImpl) { bind<UserRemoteDatasource>() }
    singleOf(::UserRepositoryImpl) { bind<UserRepository>() }

    viewModelOf(::SplashActivityViewModel)


    // Splash
    viewModelOf(::SplashActivityViewModel)

    // Authentication
    viewModelOf(::LandingFragmentViewModel)
    viewModelOf(::SignUpFragmentViewModel)
    viewModelOf(::LoginFragmentViewModel)
    viewModelOf(::AuthenticationActivityViewModel)



    single<MyApplicationContract.Presenter> {
        MyApplicationPresenter(
            get(),
            get(),
            get()
        )
    }

    single {
        val client = HttpClient(Android) {
            engine {
                socketTimeout = 100_000
                connectTimeout = 100_000
            }
            defaultRequest {
                url {
                    protocol = URLProtocol.HTTPS
                    host = BuildConfig.API_URL
                }
                headers {
                    append(HttpHeaders.ContentType, ContentType.Application.Json)
                    append("x-api-key", BuildConfig.X_API_KEY)
                    get<UserRepository>().getCacheToken()?.let { token ->
                        append("token", token)
                    }
                }
            }

            install(HttpRequestRetry) {
                retryOnServerErrors(0)
                exponentialDelay()
            }
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Log.i("HttpClient", message)
                    }
                }
                level = LogLevel.ALL
            }
            install(ContentNegotiation) {
                json(
                    Json {
                        isLenient = true
                        prettyPrint = true
                        ignoreUnknownKeys = true
                    }
                )
            }
            install(HttpCache)
            install(HttpTimeout) {
                requestTimeoutMillis = 60_000
            }

            HttpResponseValidator {
                validateResponse { response ->
                    DebugLogger.d("AppHttpClient", "validateResponse: response=$response")
                    if (!response.status.isSuccess()) {
                        throw ClientRequestException(response, "")
                    }
                }

                handleResponseExceptionWithRequest { cause, request ->
                    DebugLogger.d(
                        "AppHttpClient",
                        "handleResponseExceptionWithRequest: cause=$cause, request=$request"
                    )
                    try {
                        if (cause !is ClientRequestException) throw cause
                        val errorData = cause.response.bodyAsText()
                        val error = ApiError(errorData)
                        DebugLogger.d(
                            "AppHttpClient",
                            "handleResponseExceptionWithRequest: error=${error}, errorData=${errorData}"
                        )
                        when (error.code) {
                            401 -> {
                                if ("Invalid Token".equals(error.message, true)
                                    || "Token has expired".equals(error.message, true)
                                ) {
                                    val intent =
                                        Intent(AppConstants.Broadcast.TOKEN_EXPIRED_VIA_API_ACTION)
                                    intent.setPackage(androidApplication().packageName)
                                    androidApplication().sendBroadcast(intent)
                                }
                            }
                        }
                        throw ApiError(errorData)
                    } catch (ex: Throwable) {
                        DebugLogger.d("AppHttpClient", "response: ex=${ex}")
                        throw ex
                    }
                }
            }

        }

        client.plugin(HttpSend).intercept { request ->
            DebugLogger.d("AppHttpClient", "request=$request")
            if (request.url.encodedPath.endsWith("users/password/login", true)
                || request.url.encodedPath.endsWith("users/password/signup", true)
                || request.url.encodedPath.endsWith("users/anon/signup", true)
                || request.url.encodedPath.endsWith("users/password/forgot", true)
            ) {
                request.headers.remove("token")
            }
            execute(request)
        }
        client
    }

}