package com.library.link_attribution.repository.tracking

import com.library.link_attribution.repository.tracking.model.TrackClickModel
import com.library.link_attribution.repository.tracking.remote.api.TrackClickRequest
import kotlinx.coroutines.flow.Flow

interface TrackingRepository {
    fun onAppDied()
    fun onLoggedOut()
    fun onTokenExpired()

    fun getCacheTrackClick(): TrackClickModel?
    fun setTrackClick(link: TrackClickModel?)
    fun trackClick(
        appUnid: String?,
        apiKey: String?,
        request: TrackClickRequest
    ): Flow<TrackClickModel?>

}
