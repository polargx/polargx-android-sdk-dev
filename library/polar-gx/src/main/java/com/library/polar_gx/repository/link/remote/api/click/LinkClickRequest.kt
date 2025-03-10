package com.library.polar_gx.repository.link.remote.api.click

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LinkClickRequest(
    @SerialName("SdkUsed")
    val sdkUsed: Boolean?,
)