package com.library.link_attribution.repository.link.remote.api.matching

import com.library.link_attribution.repository.link.remote.model.LinkDTO
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetLinkByMatchingResponse(
    @SerialName("data")
    val data: Data? = null
) {

    @Serializable
    data class Data(
        @SerialName("link")
        val link: LinkDTO? = null
    )
}