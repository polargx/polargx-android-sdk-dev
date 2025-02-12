package com.library.link_attribution.repository.tracking.remote

import com.library.link_attribution.repository.tracking.remote.api.click.TrackClickRequest
import com.library.link_attribution.repository.tracking.remote.api.event.TrackEventRequest
import com.library.link_attribution.repository.tracking.remote.api.link.TrackLinkRequest
import io.ktor.client.statement.HttpResponse

interface TrackingRemoteDatasource {

    suspend fun trackClick(
        appUnid: String?,
        apiKey: String?,
        request: TrackClickRequest
    ): HttpResponse

    suspend fun trackEvent(request: TrackEventRequest): HttpResponse

    suspend fun trackLink(request: TrackLinkRequest): HttpResponse
}