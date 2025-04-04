package com.library.polargx.models

import MapModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserModel(
    @SerialName("organizationUnid")
    val organizationUnid: String?,
    @SerialName("userID")
    val userID: String?,
    @SerialName("data")
    val data: MapModel?
) {

    constructor(
        organizationUnid: String?,
        userID: String?,
        data: Map<String, Any?>?
    ) : this(organizationUnid, userID, MapModel(data))
}