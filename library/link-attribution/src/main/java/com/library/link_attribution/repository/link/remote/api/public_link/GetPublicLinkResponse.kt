package com.library.link_attribution.repository.link.remote.api.public_link

import com.library.link_attribution.repository.link.remote.model.PublicLinkDataDTO
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetPublicLinkResponse(
    @SerialName("data")
    val data: Data? = null
) {

    @Serializable
    data class Data(
        @SerialName("publicLinkData")
        val publicLinkData: PublicLinkDataDTO? = null
    )
}