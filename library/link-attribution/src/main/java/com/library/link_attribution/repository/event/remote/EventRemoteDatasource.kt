package com.library.link_attribution.repository.event.remote

import com.library.link_attribution.repository.event.remote.api.EventTrackRequest
import io.ktor.client.statement.HttpResponse

interface EventRemoteDatasource {
    suspend fun track(request: EventTrackRequest?): HttpResponse
}