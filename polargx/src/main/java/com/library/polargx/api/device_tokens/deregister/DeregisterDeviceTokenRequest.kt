package com.library.polargx.api.device_tokens.deregister

import com.library.polargx.models.DeregisterDeviceTokenModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeregisterDeviceTokenRequest(
    @SerialName("organizationUnid")
    val organizationUnid: String?,
    @SerialName("userID")
    val userID: String?,
    @SerialName("deviceToken")
    val deviceToken: String?
) {

    companion object {
        fun from(deviceToken: DeregisterDeviceTokenModel?): DeregisterDeviceTokenRequest? {
            if (deviceToken == null) return null
            return DeregisterDeviceTokenRequest(
                organizationUnid = deviceToken.organizationUnid,
                userID = deviceToken.userID,
                deviceToken = deviceToken.deviceToken
            )
        }
    }
}