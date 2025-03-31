package com.library.polargx.api.update_link

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateLinkClickRequest(
    @SerialName("SdkUsed")
    val sdkUsed: Boolean?
)