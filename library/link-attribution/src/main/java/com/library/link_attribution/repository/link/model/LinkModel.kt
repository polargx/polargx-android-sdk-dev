package com.library.link_attribution.repository.link.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class LinkModel(
    @SerialName("unid")
    val unid: String? = null,
    @SerialName("createdAt")
    val createdAt: String? = null,
    @SerialName("updatedAt")
    val updatedAt: String? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("description")
    val description: String? = null,
    @SerialName("path")
    val path: String? = null,
    @SerialName("analyticsTags")
    val analyticsTags: AnalyticsTags? = null,
    @SerialName("socialMediaPreview")
    val socialMediaPreview: SocialMediaPreview? = null,
    @SerialName("attributes")
    val attributes: Map<String?, String>? = null,
) : Parcelable {

    @Serializable
    @Parcelize
    data class AnalyticsTags(
        @SerialName("feature")
        val feature: String? = null,
        @SerialName("channel")
        val channel: String? = null,
        @SerialName("campaign")
        val campaign: String? = null,
        @SerialName("tags")
        val tags: List<String>? = null,
    ) : Parcelable

    @Serializable
    @Parcelize
    data class SocialMediaPreview(
        @SerialName("title")
        val title: String? = null,
        @SerialName("description")
        val description: String? = null,
    ) : Parcelable
}