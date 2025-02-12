package com.library.link_attribution.repository.tracking.remote.api.link

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TrackLinkRequest(
    @SerialName("additionalData")
    val additionalData: AdditionalData? = null,
    @SerialName("deviceData")
    val deviceData: DeviceData? = null,
    @SerialName("domain")
    val domain: String? = null,
    @SerialName("slug")
    val slug: String? = null,
    @SerialName("trackType")
    val trackType: String? = null,
) {

    @Serializable
    data class DeviceData(
        @SerialName("additionalProp1")
        val additionalProp1: String? = null,
        @SerialName("additionalProp2")
        val additionalProp2: String? = null,
        @SerialName("additionalProp3")
        val additionalProp3: String? = null,
    )

    @Serializable
    data class AdditionalData(
        @SerialName("additionalProp1")
        val additionalProp1: String? = null,
        @SerialName("additionalProp2")
        val additionalProp2: String? = null,
        @SerialName("additionalProp3")
        val additionalProp3: String? = null,
    )
}