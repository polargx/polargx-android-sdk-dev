package com.library.link_attribution.repository.tracking.remote

import com.library.link_attribution.repository.tracking.remote.api.TrackClickRequest
import com.library.link_attribution.repository.tracking.remote.api.TrackEventRequest
import io.ktor.client.statement.HttpResponse

interface TrackingRemoteDatasource {

    suspend fun trackClick(
        appUnid: String?,
        apiKey: String?,
        request: TrackClickRequest
    ): HttpResponse

    suspend fun trackEvent(request: TrackEventRequest): HttpResponse
}