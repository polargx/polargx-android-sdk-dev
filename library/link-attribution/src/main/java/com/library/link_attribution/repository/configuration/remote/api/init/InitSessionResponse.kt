package com.library.link_attribution.repository.configuration.remote.api.init

import com.library.link_attribution.repository.configuration.remote.model.InitSessionDTO
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InitSessionResponse(
    @SerialName("data")
    val data: Data? = null
) {

    @Serializable
    data class Data(
        @SerialName("initSession")
        val initSession: InitSessionDTO? = null
    )
}