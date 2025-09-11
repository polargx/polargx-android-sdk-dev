package com.library.polargx.api.link_click

import com.library.polargx.models.LinkClickModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LinkClickResponse(
    @SerialName("data")
    val data: Data?
) {

    @Serializable
    data class Data(
        @SerialName("linkClick")
        val linkClick: LinkClickModel?
    )
}