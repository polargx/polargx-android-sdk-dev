package com.library.link_attribution.repository.link

import com.data.shared.ApiError
import com.library.link_attribution.repository.link.local.LinkLocalDatasource
import com.library.link_attribution.repository.link.local.model.LinkEntity.Companion.toEntity
import com.library.link_attribution.repository.link.model.LinkModel
import com.library.link_attribution.repository.link.remote.LinkRemoteDatasource
import com.library.link_attribution.repository.link.remote.api.matching.GetLinkByMatchingRequest
import com.library.link_attribution.repository.link.remote.api.matching.GetLinkByMatchingResponse
import com.library.link_attribution.repository.link.remote.api.path.GetLinkByPathResponse
import io.ktor.client.call.body
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LinkRepositoryImpl(
    private val localDatasource: LinkLocalDatasource,
    private val remoteDatasource: LinkRemoteDatasource
) : LinkRepository {

    companion object {
        const val TAG = ">>>LinkRepositoryImpl"
    }

    private var mLink: LinkModel? = null

    override fun onAppDied() {
        mLink = null
    }

    private fun onUnauthenticated() {
        mLink = null
    }

    override fun onLoggedOut() {
        onUnauthenticated()
    }

    override fun onTokenExpired() {
        onUnauthenticated()
    }

    override fun getCacheLink(): LinkModel? {
        if (mLink == null) {
            mLink = localDatasource.getLink()?.toExternal()
        }
        return mLink
    }

    override fun setLink(link: LinkModel?) {
        mLink = link
        localDatasource.setLink(link?.toEntity())
    }

    override fun fetchAndCacheLinkByPath(
        appUnid: String?,
        apiKey: String?,
        path: String?
    ): Flow<LinkModel?> {
        return flow {
            val response = remoteDatasource.getLinkByPath(
                appUnid = appUnid,
                apiKey = apiKey,
                path = path
            )
            if (response.status.isSuccess()) {
                val body = response.body<GetLinkByPathResponse>()
                mLink = body.data?.link?.toExternal()
                localDatasource.setLink(mLink?.toEntity())
                emit(mLink)
            } else {
                throw ApiError(response.bodyAsText())
            }
        }
    }

    override fun fetchAndCacheLinkByMatching(
        appUnid: String?,
        apiKey: String?,
        request: GetLinkByMatchingRequest
    ): Flow<LinkModel?> {
        return flow {
            val response = remoteDatasource.getLinkByMatching(
                appUnid = appUnid,
                apiKey = apiKey,
                request = request
            )
            if (response.status.isSuccess()) {
                val body = response.body<GetLinkByMatchingResponse>()
                mLink = body.data?.link?.toExternal()
                localDatasource.setLink(mLink?.toEntity())
                emit(mLink)
            } else {
                throw ApiError(response.bodyAsText())
            }
        }
    }
}
