package com.library.polargx.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LinkDataModel(
    @SerialName("data")
    val data: MapModel?
)