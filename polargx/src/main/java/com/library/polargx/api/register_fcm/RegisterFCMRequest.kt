package com.library.polargx.api.register_fcm

import com.library.polargx.models.RegisterFCMModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterFCMRequest(
    @SerialName("organizationUnid")
    val organizationUnid: String?,
    @SerialName("userID")
    val userID: String?,
    @SerialName("fcmToken")
    val fcmToken: String?
) {

    companion object {
        fun from(fcm: RegisterFCMModel?): RegisterFCMRequest? {
            if (fcm == null) return null
            return RegisterFCMRequest(
                organizationUnid = fcm.organizationUnid,
                userID = fcm.userID,
                fcmToken = fcm.fcmToken
            )
        }
    }
}