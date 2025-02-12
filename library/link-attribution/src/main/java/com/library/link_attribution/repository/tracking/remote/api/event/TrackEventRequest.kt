package com.library.link_attribution.repository.tracking.remote.api.event

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TrackEventRequest(
    @SerialName("organizationUnid")
    val organizationUnid: String? = null,
    @SerialName("eventName")
    val eventName: String? = null,
    @SerialName("data")
    val data: Data? = null
) {

    @Serializable
    data class Data(
        @SerialName("additionalProp1")
        val additionalProp1: String? = null,
        @SerialName("additionalProp2")
        val additionalProp2: String? = null,
        @SerialName("additionalProp3")
        val additionalProp3: String? = null,
    )
}