package com.library.link_attribution.repository.link

import com.library.link_attribution.repository.link.remote.api.click.LinkClickRequest
import com.library.link_attribution.repository.link.remote.api.click.LinkClickResponse
import com.library.link_attribution.repository.link.remote.api.link.GetLinkDataResponse
import com.library.link_attribution.repository.link.remote.api.matches.GetLinkMatchesResponse
import com.library.link_attribution.repository.link.remote.api.organization.GetOrganizationResponse
import com.library.link_attribution.repository.link.remote.api.track.LinkTrackRequest
import com.library.link_attribution.repository.link.remote.api.track.LinkTrackResponse

interface LinkRepository {
    suspend fun onAppDied()
    suspend fun onLoggedOut()
    suspend fun onTokenExpired()

    suspend fun fetchLinkData(
        domain: String?,
        slug: String?,
    ): GetLinkDataResponse

    suspend fun fetchOrganization(
        domain: String?
    ): GetOrganizationResponse

    suspend fun fetchLinkMatches(
        fingerprint: String?
    ): GetLinkMatchesResponse

    suspend fun track(
        request: LinkTrackRequest?
    ): LinkTrackResponse

    suspend fun linkClick(
        linkClickUnid: String?,
        request: LinkClickRequest?
    ): LinkClickResponse

    suspend fun reset()
}
