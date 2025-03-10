package com.library.polar_gx.repository.link

import com.library.polar_gx.model.ApiError
import com.library.polar_gx.repository.link.local.LinkLocalDatasource
import com.library.polar_gx.repository.link.remote.LinkRemoteDatasource
import com.library.polar_gx.repository.link.remote.api.click.LinkClickRequest
import com.library.polar_gx.repository.link.remote.api.click.LinkClickResponse
import com.library.polar_gx.repository.link.remote.api.link.GetLinkDataResponse
import com.library.polar_gx.repository.link.remote.api.matches.GetLinkMatchesResponse
import com.library.polar_gx.repository.link.remote.api.organization.GetOrganizationResponse
import com.library.polar_gx.repository.link.remote.api.track.LinkTrackRequest
import com.library.polar_gx.repository.link.remote.api.track.LinkTrackResponse
import io.ktor.client.call.body
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess

class LinkRepositoryImpl(
    private val localDatasource: LinkLocalDatasource,
    private val remoteDatasource: LinkRemoteDatasource
) : LinkRepository {

    companion object {
        const val TAG = ">>>LinkRepositoryImpl"
    }

    override suspend fun onAppDied() {
    }

    private fun onUnauthenticated() {
    }

    override suspend fun onLoggedOut() {
        onUnauthenticated()
    }

    override suspend fun onTokenExpired() {
        onUnauthenticated()
    }

    override suspend fun fetchLinkData(
        domain: String?,
        slug: String?
    ): GetLinkDataResponse {
        if (domain == null) throw Throwable("domain is null")
        if (slug == null) throw Throwable("slug is null")

        val response = remoteDatasource.fetchLinkData(
            domain = domain,
            slug = slug
        )
        if (!response.status.isSuccess()) {
            throw ApiError(response.bodyAsText())
        }
        return response.body<GetLinkDataResponse>()
    }

    override suspend fun fetchOrganization(
        domain: String?
    ): GetOrganizationResponse {
        if (domain == null) throw Throwable("domain is null")
        val response = remoteDatasource.fetchOrganization(
            domain = domain,
        )
        if (!response.status.isSuccess()) {
            throw ApiError(response.bodyAsText())
        }
        return response.body<GetOrganizationResponse>()
    }

    override suspend fun fetchLinkMatches(
        fingerprint: String?
    ): GetLinkMatchesResponse {
        if (fingerprint == null) throw Throwable("fingerprint is null")
        val response = remoteDatasource.fetchLinkMatches(
            fingerprint = fingerprint,
        )
        if (!response.status.isSuccess()) {
            throw ApiError(response.bodyAsText())
        }
        return response.body<GetLinkMatchesResponse>()
    }

    override suspend fun track(
        request: LinkTrackRequest?
    ): LinkTrackResponse {
        val response = remoteDatasource.track(
            request = request,
        )
        if (!response.status.isSuccess()) {
            throw ApiError(response.bodyAsText())
        }
        return response.body<LinkTrackResponse>()
    }

    override suspend fun linkClick(
        linkClickUnid: String?,
        request: LinkClickRequest?
    ): LinkClickResponse {
        val response = remoteDatasource.linkClick(
            linkClickUnid = linkClickUnid,
            request = request,
        )
        if (!response.status.isSuccess()) {
            throw ApiError(response.bodyAsText())
        }
        return response.body<LinkClickResponse>()
    }

    override suspend fun reset() {

    }
}
