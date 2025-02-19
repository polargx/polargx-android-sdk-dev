package com.library.link_attribution.di

import android.content.Context
import android.util.Log
import com.library.link_attribution.BuildConfig
import com.library.link_attribution.LinkAttribution
import com.library.link_attribution.LinkAttribution.Companion.TAG
import com.library.link_attribution.logger.LALogger
import com.library.link_attribution.model.ApiError
import com.library.link_attribution.repository.event.EventRepository
import com.library.link_attribution.repository.event.EventRepositoryImpl
import com.library.link_attribution.repository.event.local.EventLocalDatasource
import com.library.link_attribution.repository.event.local.EventLocalDatasourceImpl
import com.library.link_attribution.repository.event.remote.EventRemoteDatasource
import com.library.link_attribution.repository.event.remote.EventRemoteDatasourceImpl
import com.library.link_attribution.repository.link.LinkRepository
import com.library.link_attribution.repository.link.LinkRepositoryImpl
import com.library.link_attribution.repository.link.local.LinkLocalDatasource
import com.library.link_attribution.repository.link.local.LinkLocalDatasourceImpl
import com.library.link_attribution.repository.link.remote.LinkRemoteDatasource
import com.library.link_attribution.repository.link.remote.LinkRemoteDatasourceImpl
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.headers
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.URLProtocol
import io.ktor.http.append
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val linkAttributeModule = module {
    // Repositories
    singleOf(::LinkLocalDatasourceImpl) { bind<LinkLocalDatasource>() }
    singleOf(::LinkRemoteDatasourceImpl) { bind<LinkRemoteDatasource>() }
    singleOf(::LinkRepositoryImpl) { bind<LinkRepository>() }

    singleOf(::EventLocalDatasourceImpl) { bind<EventLocalDatasource>() }
    singleOf(::EventRemoteDatasourceImpl) { bind<EventRemoteDatasource>() }
    singleOf(::EventRepositoryImpl) { bind<EventRepository>() }


    single {
        androidApplication().getSharedPreferences(
            "linkAttribution.file",
            Context.MODE_PRIVATE
        )
    }

    single {
        val client = HttpClient(Android) {
            engine {
                socketTimeout = 60_000
                connectTimeout = 60_000
            }
            defaultRequest {
                url {
//                    protocol = URLProtocol.HTTP
                    protocol = URLProtocol.HTTPS
                    host = BuildConfig.API_URL
                }
                headers {
                    append(HttpHeaders.ContentType, ContentType.Application.Json)
                    append("x-api-key", LinkAttribution.getConfigs()?.apiKey ?: "")
                }
            }

            install(HttpRequestRetry) {
                retryOnServerErrors(0)
                exponentialDelay()
            }
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        LALogger.i("HttpClient", message)
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
                    LALogger.d(TAG, "validateResponse: response=$response")
                    if (!response.status.isSuccess()) {
                        throw ClientRequestException(response, "")
                    }
                }

                handleResponseExceptionWithRequest { cause, request ->
                    LALogger.d(
                        TAG,
                        "handleResponseExceptionWithRequest: cause=$cause, request=$request"
                    )
                    try {
                        if (cause !is ClientRequestException) throw cause
                        val errorData = cause.response.bodyAsText()
                        val error = ApiError(errorData)
                        LALogger.d(
                            TAG,
                            "handleResponseExceptionWithRequest: error=${error}, errorData=${errorData}"
                        )
                        when (error.code) {

                        }
                        throw ApiError(errorData)
                    } catch (ex: Throwable) {
                        LALogger.d(TAG, "response: ex=${ex}")
                        throw ex
                    }
                }
            }

        }

//        client.plugin(HttpSend).intercept { request ->
//            LALogger.d(TAG, "request=$request")
//            if (request.url.encodedPath.endsWith("users/password/login", true)
//                || request.url.encodedPath.endsWith("users/password/signup", true)
//                || request.url.encodedPath.endsWith("users/anon/signup", true)
//                || request.url.encodedPath.endsWith("users/password/forgot", true)
//            ) {
//                request.headers.remove("token")
//            }
//            execute(request)
//        }
        client
    }
}