package com.library.polargx.api.update_user

import com.library.polargx.models.MapModel
import com.library.polargx.models.UpdateUserModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserRequest(
    @SerialName("organizationUnid")
    val organizationUnid: String?,
    @SerialName("userID")
    val userID: String?,
    @SerialName("data")
    val data: MapModel?
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