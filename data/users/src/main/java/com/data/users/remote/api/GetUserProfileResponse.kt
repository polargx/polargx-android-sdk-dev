package com.data.users.remote.api

import com.data.users.remote.model.UserDTO
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetUserProfileResponse(
    @SerialName("data")
    val data: Data? = null
) {

    @Serializable
    data class Data(
        @SerialName("user")
        val user: UserDTO? = null
    )
}