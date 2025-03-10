package com.library.polar_gx.repository.event.remote

import com.library.polar_gx.repository.event.remote.api.EventTrackRequest
import io.ktor.client.statement.HttpResponse

interface EventRemoteDatasource {
    suspend fun track(request: EventTrackRequest?): HttpResponse
}