package com.library.link_attribution.repository.event.model

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
    )
    annotation class Type {
        companion object {
            const val APP_LAUNCH = "app_launch"
        }
    }
}