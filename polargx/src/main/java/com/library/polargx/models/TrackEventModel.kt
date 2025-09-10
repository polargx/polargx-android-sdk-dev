package com.library.polargx.models

import DictionaryModel
import androidx.annotation.StringDef
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TrackEventModel(
    @SerialName("organizationUnid")
    val organizationUnid: String?,
    @SerialName("userID")
    val userID: String?,
    @SerialName("eventName")
    val eventName: String?,
    @SerialName("eventTime")
    val eventTime: String?,
    @SerialName("data")
    val data: DictionaryModel?
) {

    constructor(
        organizationUnid: String?,
        userID: String?,
        eventName: String?,
        eventTime: String?,
        data: Map<String, Any?>?
    ) : this(organizationUnid, userID, eventName, eventTime, DictionaryModel(data))

    @Retention(value = AnnotationRetention.SOURCE)
    @StringDef(
        Type.APP_LAUNCH,
        Type.APP_OPEN,
        Type.APP_CLOSE,
        Type.APP_ACTIVE,
        Type.APP_INACTIVE,
        Type.APP_TERMINATE,
        Type.APP_UNKNOWN_LIFECYCLE,
    )
    annotation class Type {
        companion object {
            const val APP_LAUNCH = "app_launch"
            const val APP_OPEN = "app_open"
            const val APP_CLOSE = "app_close"
            const val APP_ACTIVE = "app_active"
            const val APP_INACTIVE = "app_inactive"
            const val APP_TERMINATE = "app_terminate"
            const val APP_UNKNOWN_LIFECYCLE = "unknown_lifecycle"
        }
    }
}