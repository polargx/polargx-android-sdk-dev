package com.library.polar_gx.repository.link.remote

import com.library.polar_gx.repository.link.remote.api.click.LinkClickRequest
import com.library.polar_gx.repository.link.remote.api.track.LinkTrackRequest
import io.ktor.client.statement.HttpResponse

interface LinkRemoteDatasource {
    suspend fun fetchLinkData(
        domain: String,
        slug: String
    ): HttpResponse

    suspend fun fetchOrganization(
        domain: String
    ): HttpResponse

    suspend fun fetchLinkMatches(
        fingerprint: String
    ): HttpResponse

    suspend fun track(
        request: LinkTrackRequest?
    ): HttpResponse

    suspend fun linkClick(
        linkClickUnid: String?,
        request: LinkClickRequest?
    ): HttpResponse
}