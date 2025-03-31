package com.library.polargx.di

import android.content.Context
import com.library.polargx.PolarApp
import com.library.polargx.PolarApp.Companion.TAG
import com.library.polargx.api.ApiService
import com.library.polargx.api.ApiServiceImpl
import com.library.polargx.Configuration
import com.library.polargx.helpers.ApiError
import com.library.polargx.helpers.Logger
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
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import io.ktor.client.plugins.logging.Logger as HttpLogger

val polarModule = module {

    singleOf(::ApiServiceImpl) bind ApiService::class

    single {
        androidApplication().getSharedPreferences(
            "polar_gx.file",
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
                    protocol = URLProtocol.HTTPS
                    host = Configuration.Env.server
                }
                headers {
                    append(HttpHeaders.ContentType, ContentType.Application.Json)
                    append("x-api-key", PolarApp.shared.apiKey)
                }
            }

            install(HttpRequestRetry) {
                retryOnServerErrors(0)
                exponentialDelay()
            }
            install(Logging) {
                logger = object : HttpLogger {
                    override fun log(message: String) {
                        Logger.i("HttpClient", message)
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
                    Logger.d(TAG, "validateResponse: response=$response")
                    if (!response.status.isSuccess()) {
                        throw ClientRequestException(response, "")
                    }
                }

                handleResponseExceptionWithRequest { cause, request ->
                    Logger.d(
                        TAG,
                        "handleResponseExceptionWithRequest: cause=$cause, request=$request"
                    )
                    try {
                        if (cause !is ClientRequestException) throw cause
                        val errorData = cause.response.bodyAsText()
                        val error = ApiError(errorData)
                        Logger.d(
                            TAG,
                            "handleResponseExceptionWithRequest: error=${error}, errorData=${errorData}"
                        )
                    } catch (ex: Throwable) {
                        Logger.d(TAG, "response: ex=${ex}")
                        throw ex
                    }
                }
            }

        }
        client
    }
}