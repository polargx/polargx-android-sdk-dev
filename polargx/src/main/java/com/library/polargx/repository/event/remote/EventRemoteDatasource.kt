package com.library.polargx.repository.event.remote

import com.library.polargx.repository.event.remote.api.EventTrackRequest
import io.ktor.client.statement.HttpResponse

interface EventRemoteDatasource {
    suspend fun track(request: EventTrackRequest?): HttpResponse
}