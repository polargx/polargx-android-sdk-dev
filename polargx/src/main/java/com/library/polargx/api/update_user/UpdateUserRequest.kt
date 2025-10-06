package com.library.polargx.api.update_user

import com.library.polargx.models.DictionaryModel
import com.library.polargx.models.UpdateUserModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserRequest(
    @SerialName("clobberMatchingAttributes")
    val clobberMatchingAttributes: Boolean?,
    @SerialName("organizationUnid")
    val organizationUnid: String?,
    @SerialName("userID")
    val userID: String?,
    @SerialName("data")
    val data: DictionaryModel?
) {

    companion object {
        fun from(user: UpdateUserModel?): UpdateUserRequest? {
            if (user == null) return null
            return UpdateUserRequest(
                clobberMatchingAttributes = user.clobberMatchingAttributes,
                organizationUnid = user.organizationUnid,
                userID = user.userID,
                data = user.data
            )
        }
    }
}