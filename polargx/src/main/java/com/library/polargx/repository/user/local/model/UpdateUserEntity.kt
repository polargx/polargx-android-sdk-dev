package com.library.polargx.repository.user.local.model

import com.library.polargx.repository.user.model.UpdateUserModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserEntity(
    @SerialName("organizationUnid")
    val organizationUnid: String? = null,
    @SerialName("userID")
    val userID: String? = null,
    @SerialName("data")
    val data: Map<String, String>? = null
) {

    fun toExternal(): UpdateUserModel {
        return UpdateUserModel(
            organizationUnid = organizationUnid,
            userID = userID,
            data = data
        )
    }

    companion object {
        fun UpdateUserModel?.toEntity(): UpdateUserEntity? {
            if (this == null) return null
            return UpdateUserEntity(
                organizationUnid = organizationUnid,
                userID = userID,
                data = data
            )
        }
    }
}