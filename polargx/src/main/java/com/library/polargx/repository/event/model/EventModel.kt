package com.library.polargx.repository.event.model

import android.os.Parcelable
import androidx.annotation.StringDef
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class EventModel(
    @SerialName("organizationUnid")
    val organizationUnid: String? = null,
    @SerialName("eventName")
    val eventName: String? = null,
    @SerialName("eventTime")
    val eventTime: String? = null,//2025-02-18T07:26:26.735Z
    @SerialName("data")
    val data: Map<String, String?>? = null
) : Parcelable {

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