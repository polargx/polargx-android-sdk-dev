package com.library.link_attribution.repository.link.remote.api.path

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetLinkByPathRequest(
    @SerialName("path")
    val path: String? = null
)