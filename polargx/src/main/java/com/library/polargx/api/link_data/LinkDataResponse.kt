package com.library.polargx.api.link_data

import com.library.polargx.models.LinkDataModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LinkDataResponse(
    @SerialName("sdkLinkData")
    val sdkLinkData: LinkDataModel?
)