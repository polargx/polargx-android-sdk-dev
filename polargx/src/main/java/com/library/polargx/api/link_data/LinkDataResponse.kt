package com.library.polargx.api.link_data

import com.library.polargx.models.LinkDataModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LinkDataResponse(
    @SerialName("data")
    val data: Data?
) {

    @Serializable
    data class Data(
        @SerialName("sdkLinkData")
        val sdkLinkData: LinkDataModel?
    )
}