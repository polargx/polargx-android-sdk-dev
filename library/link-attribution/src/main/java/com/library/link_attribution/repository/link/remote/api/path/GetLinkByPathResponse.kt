package com.library.link_attribution.repository.link.remote.api.path

import com.library.link_attribution.repository.link.remote.model.LinkDTO
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetLinkByPathResponse(
    @SerialName("data")
    val data: Data? = null,
    @SerialName("api_version")
    val apiVersion: Double? = null
) {

    @Serializable
    data class Data(
        @SerialName("link")
        val link: LinkDTO? = null
    )
}