package com.library.polargx.api.deregister_fcm

import com.library.polargx.models.DeregisterFCMModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeregisterFCMRequest(
    @SerialName("organizationUnid")
    val organizationUnid: String?,
    @SerialName("userID")
    val userID: String?,
    @SerialName("fcmToken")
    val fcmToken: String?
) {

    companion object {
        fun from(fcm: DeregisterFCMModel?): DeregisterFCMRequest? {
            if (fcm == null) return null
            return DeregisterFCMRequest(
                organizationUnid = fcm.organizationUnid,
                userID = fcm.userID,
                fcmToken = fcm.fcmToken
            )
        }
    }
}