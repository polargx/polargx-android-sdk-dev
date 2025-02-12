package com.library.link_attribution.repository.tracking.remote.api.click

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TrackClickRequest(
    @SerialName("path")
    val path: String? = null,
    @SerialName("deviceInfo")
    val deviceInfo: DeviceInfo? = null,
    @SerialName("screenSize")
    val screenSize: ScreenSize? = null
) {

    @Serializable
    data class DeviceInfo(
        @SerialName("name")
        val name: String? = null,
        @SerialName("model")
        val model: String? = null,
        @SerialName("manufacturer")
        val manufacturer: String? = null,
        @SerialName("osVersion")
        val osVersion: String? = null,
        @SerialName("sdkVersion")
        val sdkVersion: Int? = null,
        @SerialName("ip6Address")
        val ip6Address: String? = null,
        @SerialName("ip4Address")
        val ip4Address: String? = null,
    )

    @Serializable
    data class ScreenSize(
        @SerialName("width")
        val width: Int? = null,
        @SerialName("height")
        val height: Int? = null,
        @SerialName("resolution")
        val resolution: String? = null,
    )
}