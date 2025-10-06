package com.library.polargx.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

typealias DeregisterFCMModel = RegisterFCMModel

@Serializable
data class RegisterFCMModel(
    @SerialName("organizationUnid")
    val organizationUnid: String,
    @SerialName("userID")
    val userID: String,
    @SerialName("fcmToken")
    val fcmToken: String
)
