package com.library.link_attribution.repository.tracking.remote

import com.library.link_attribution.repository.tracking.remote.api.click.TrackClickRequest
import com.library.link_attribution.repository.tracking.remote.api.event.TrackEventRequest
import com.library.link_attribution.repository.tracking.remote.api.link.TrackLinkRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.path

class TrackingRemoteDatasourceImpl(
    private val client: HttpClient
) : TrackingRemoteDatasource {

    companion object {
        const val TAG = ">>>TrackingRemoteDatasourceImpl"
    }

    override suspend fun trackClick(
        appUnid: String?,
        apiKey: String?,
        request: TrackClickRequest
    ): HttpResponse {
        if (appUnid == null) throw Throwable(message = "appUnid is null!")
        if (apiKey == null) throw Throwable(message = "apiKey is null!")
        return client.post {
            url.path("trackClick")
            headers.append("app-unid", appUnid)
            headers.append("api-key", apiKey)
            setBody(request)
        }
    }

    override suspend fun trackEvent(request: TrackEventRequest): HttpResponse {
        return client.post {
            url.path("sdk/v1/events/track")
            setBody(request)
        }
    }

    override suspend fun trackLink(request: TrackLinkRequest): HttpResponse {
        return client.post {
            url.path("sdk/v1/links/track")
            setBody(request)
        }
    }
}