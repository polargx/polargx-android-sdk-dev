package com.library.link_attribution.repository.configuration.remote.api

import com.library.link_attribution.repository.configuration.remote.model.ConfigurationDTO
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetConfigurationResponse(
    @SerialName("data")
    val data: Data? = null
) {

    @Serializable
    data class Data(
        @SerialName("configuration")
        val configuration: ConfigurationDTO? = null
    )
}