package com.library.polargx.api.device_tokens.register

import com.library.polargx.models.RegisterDeviceTokenModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterDeviceTokenRequest(
    @SerialName("organizationUnid")
    val organizationUnid: String?,
    @SerialName("userID")
    val userID: String?,
    @SerialName("deviceToken")
    val deviceToken: String?
) {

    companion object {
        fun from(deviceToken: RegisterDeviceTokenModel?): RegisterDeviceTokenRequest? {
            if (deviceToken == null) return null
            return RegisterDeviceTokenRequest(
                organizationUnid = deviceToken.organizationUnid,
                userID = deviceToken.userID,
                deviceToken = deviceToken.deviceToken
            )
        }
    }
}