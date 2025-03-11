package com.library.polargx.repository.link.remote.api.click

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LinkClickRequest(
    @SerialName("SdkUsed")
    val sdkUsed: Boolean?,
)