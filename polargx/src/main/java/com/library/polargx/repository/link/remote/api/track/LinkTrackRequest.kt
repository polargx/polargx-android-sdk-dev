package com.library.polargx.repository.link.remote.api.track

import androidx.annotation.StringDef
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LinkTrackRequest(
    @SerialName("domain")
    val domain: String?,
    @SerialName("slug")
    val slug: String?,
    @SerialName("trackType")
    val trackType: String?,
    @SerialName("clickTime")
    val clickTime: String?,
    @SerialName("fingerprint")
    val fingerprint: String?,
    @SerialName("deviceData")
    val deviceData: Map<String, String>?,
    @SerialName("additionalData")
    val additionalData: Map<String, String>?,
) {

    @Retention(value = AnnotationRetention.SOURCE)
    @StringDef(
        TrackType.APP_CLICK,
    )
    annotation class TrackType {
        companion object {
            const val APP_CLICK = "app_click"
        }
    }

    @Retention(value = AnnotationRetention.SOURCE)
    @StringDef(
        Fingerprint.ANDROID_SDK,
    )
    annotation class Fingerprint {
        companion object {
            const val ANDROID_SDK = "AndroidSDK"
        }
    }
}