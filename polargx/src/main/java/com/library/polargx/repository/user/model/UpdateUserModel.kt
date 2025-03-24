package com.library.polargx.repository.user.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserModel(
    @SerialName("organizationUnid")
    val organizationUnid: String? = null,
    @SerialName("userID")
    val userID: String? = null,
    @SerialName("data")
    val data: Map<String, String>? = null
)