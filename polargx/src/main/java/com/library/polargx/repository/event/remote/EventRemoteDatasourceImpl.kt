package com.library.polargx.repository.event.remote

import com.library.polargx.repository.event.remote.api.EventTrackRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.path

class EventRemoteDatasourceImpl(
    private val client: HttpClient
) : EventRemoteDatasource {

    companion object {
        const val TAG = ">>>EventRemoteDatasourceImpl"
    }

    override suspend fun track(request: EventTrackRequest?): HttpResponse {
        return client.post {
            url.path("sdk/v1/events/track")
            setBody(request)
        }
    }
}