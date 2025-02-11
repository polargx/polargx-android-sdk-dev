package com.library.link_attribution.repository.tracking.remote.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TrackEventRequest(
    @SerialName("organizationUnid")
    val organizationUnid: String? = null,
    @SerialName("eventName")
    val eventName: String? = null,
    @SerialName("data")
    val data: Map<String, String>? = null
)