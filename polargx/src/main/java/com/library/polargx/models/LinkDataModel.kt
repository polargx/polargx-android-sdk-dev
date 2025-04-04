package com.library.polargx.models

import MapModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LinkDataModel(
    @SerialName("data")
    val data: MapModel?
)