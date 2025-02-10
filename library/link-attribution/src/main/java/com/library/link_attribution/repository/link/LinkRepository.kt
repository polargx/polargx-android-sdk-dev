package com.library.link_attribution.repository.link

import com.library.link_attribution.repository.link.model.LinkModel
import com.library.link_attribution.repository.link.remote.api.matching.GetLinkByMatchingRequest
import kotlinx.coroutines.flow.Flow

interface LinkRepository {
    fun onAppDied()
    fun onLoggedOut()
    fun onTokenExpired()

    fun getCacheLink(): LinkModel?
    fun setLink(link: LinkModel?)

    fun fetchAndCacheLinkByPath(
        appUnid: String?,
        apiKey: String?,
        path: String?,
    ): Flow<LinkModel?>

    fun fetchAndCacheLinkByMatching(
        appUnid: String?,
        apiKey: String?,
        request: GetLinkByMatchingRequest
    ): Flow<LinkModel?>

}
