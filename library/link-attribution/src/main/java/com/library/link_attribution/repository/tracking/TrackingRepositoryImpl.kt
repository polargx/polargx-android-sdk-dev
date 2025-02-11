package com.library.link_attribution.repository.tracking

import com.data.shared.ApiError
import com.library.link_attribution.repository.tracking.local.TrackingLocalDatasource
import com.library.link_attribution.repository.tracking.local.model.TrackClickEntity.Companion.toEntity
import com.library.link_attribution.repository.tracking.model.TrackClickModel
import com.library.link_attribution.repository.tracking.remote.TrackingRemoteDatasource
import com.library.link_attribution.repository.tracking.remote.api.TrackClickRequest
import com.library.link_attribution.repository.tracking.remote.api.TrackClickResponse
import com.library.link_attribution.repository.tracking.remote.api.TrackEventRequest
import com.library.link_attribution.repository.tracking.remote.api.TrackEventResponse
import io.ktor.client.call.body
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TrackingRepositoryImpl(
    private val localDatasource: TrackingLocalDatasource,
    private val remoteDatasource: TrackingRemoteDatasource
) : TrackingRepository {

    companion object {
        const val TAG = ">>>TrackingRepositoryImpl"
    }

    private var mTrackClick: TrackClickModel? = null

    override fun onAppDied() {
        mTrackClick = null
    }

    private fun onUnauthenticated() {
        mTrackClick = null
    }

    override fun onLoggedOut() {
        onUnauthenticated()
    }

    override fun onTokenExpired() {
        onUnauthenticated()
    }

    override fun getCacheTrackClick(): TrackClickModel? {
        if (mTrackClick == null) {
            mTrackClick = localDatasource.getTrackClick()?.toExternal()
        }
        return mTrackClick
    }

    override fun setTrackClick(link: TrackClickModel?) {
        mTrackClick = link
        localDatasource.setTrackClick(link?.toEntity())
    }

    override fun trackClick(
        appUnid: String?,
        apiKey: String?,
        request: TrackClickRequest
    ): Flow<TrackClickModel?> {
        return flow {
            val response = remoteDatasource.trackClick(
                appUnid = appUnid,
                apiKey = apiKey,
                request = request
            )
            if (response.status.isSuccess()) {
                val body = response.body<TrackClickResponse>()
                mTrackClick = body.data?.trackClick?.toExternal()
                localDatasource.setTrackClick(mTrackClick?.toEntity())
                emit(mTrackClick)
            } else {
                throw ApiError(response.bodyAsText())
            }
        }
    }

    override fun trackEvent(request: TrackEventRequest): Flow<Unit> {
        return flow {
            val response = remoteDatasource.trackEvent(request)
            if (response.status.isSuccess()) {
                val body = response.body<TrackEventResponse>()
                emit(Unit)
            } else {
                throw ApiError(response.bodyAsText())
            }
        }
    }
}
