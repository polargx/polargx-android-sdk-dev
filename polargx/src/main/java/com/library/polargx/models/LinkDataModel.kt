package com.library.polargx.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class LinkDataModel(
    @SerialName("analyticsTags")
    val analyticsTags: AnalyticsTags?,
    @SerialName("data")
    val data: Map<String, String>?,
    @SerialName("slug")
    val slug: String?,
    @SerialName("socialMediaTags")
    val socialMediaTags: SocialMediaTags?
) : Parcelable {

    @Parcelize
    @Serializable
    data class AnalyticsTags(
        @SerialName("campaign")
        val campaign: String?,
        @SerialName("channel")
        val channel: String?,
        @SerialName("feature")
        val feature: String?,
        @SerialName("tags")
        val tags: String?
    ) : Parcelable

    @Parcelize
    @Serializable
    data class SocialMediaTags(
        @SerialName("description")
        val description: String?,
        @SerialName("title")
        val title: String?
    ) : Parcelable
}