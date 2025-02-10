package com.library.link_attribution.repository.tracking.remote.api

import com.library.link_attribution.repository.tracking.remote.model.TrackClickDTO
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TrackClickResponse(
    @SerialName("data")
    val data: Data? = null
) {

    @Serializable
    data class Data(
        @SerialName("trackClick")
        val trackClick: TrackClickDTO? = null
    )
}