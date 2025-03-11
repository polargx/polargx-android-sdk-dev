package com.library.polargx.repository.event.remote.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class EventTrackResponse(
    @SerialName("api_version")
    val apiVersion: Int? = null
)