package com.library.polargx.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

typealias DeregisterDeviceTokenModel = RegisterDeviceTokenModel

@Serializable
data class RegisterDeviceTokenModel(
    @SerialName("organizationUnid")
    val organizationUnid: String,
    @SerialName("userID")
    val userID: String,
    @SerialName("deviceToken")
    val deviceToken: String
)
