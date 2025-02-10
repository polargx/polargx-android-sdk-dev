package com.library.link_attribution.repository.configuration.remote

import com.library.link_attribution.repository.configuration.remote.api.init.InitSessionRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.HttpClientCall
import io.ktor.client.request.HttpRequest
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.DefaultHttpResponse
import io.ktor.client.statement.HttpResponse
import io.ktor.client.utils.EmptyContent.status
import io.ktor.http.headers
import io.ktor.http.path
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.delay
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class ConfigurationRemoteDatasourceImpl(
    private val client: HttpClient
) : ConfigurationRemoteDatasource {

    companion object {
        const val TAG = ">>>ConfigurationRemoteDatasourceImpl"
    }

    override suspend fun initSession(
        appUnid: String?,
        apiKey: String?, request: InitSessionRequest
    ): HttpResponse {
        if (appUnid == null) throw Throwable(message = "appUnid is null!")
        if (apiKey == null) throw Throwable(message = "apiKey is null!")
        return client.post {
            url.path("initSession")
            headers.append("app-unid", appUnid)
            headers.append("api-key", apiKey)
            setBody(request)
        }
    }

    override suspend fun getConfiguration(appUnid: String?, apiKey: String?): HttpResponse {
        if (appUnid == null) throw Throwable(message = "appUnid is null!")
        if (apiKey == null) throw Throwable(message = "apiKey is null!")
        return client.get {
            url.path("configuration")
            headers.append("app-unid", appUnid)
            headers.append("api-key", apiKey)
        }
    }

}