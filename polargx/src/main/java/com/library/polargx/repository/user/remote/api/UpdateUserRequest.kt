package com.library.polargx.repository.user.remote.api

import com.library.polargx.repository.user.model.UpdateUserModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserRequest(
    @SerialName("organizationUnid")
    val organizationUnid: String? = null,
    @SerialName("userID")
    val userID: String? = null,
    @SerialName("data")
    val data: Map<String, String>? = null
) {

    companion object {
        fun from(user: UpdateUserModel?): UpdateUserRequest? {
            if (user == null) return null
            return UpdateUserRequest(
                organizationUnid = user.organizationUnid,
                userID = user.userID,
                data = user.data
            )
        }
    }
}