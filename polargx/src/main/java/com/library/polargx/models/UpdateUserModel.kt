package com.library.polargx.models

import DictionaryModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserModel(
    @SerialName("clobberMatchingAttributes")
    val clobberMatchingAttributes: Boolean?,
    @SerialName("organizationUnid")
    val organizationUnid: String?,
    @SerialName("userID")
    val userID: String?,
    @SerialName("data")
    val data: DictionaryModel?
) {

    constructor(
        organizationUnid: String?,
        userID: String?,
        data: Map<String, Any?>?
    ) : this(
        clobberMatchingAttributes = true,
        organizationUnid = organizationUnid,
        userID = userID,
        data = DictionaryModel(data)
    )
}