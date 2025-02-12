package com.library.link_attribution.repository.link.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PublicLinkDataModel(
    @SerialName("analyticsTags")
    val analyticsTags: LinkAnalyticsTagsModel? = null,
    @SerialName("redirects")
    val redirects: LinkRedirectsModel? = null,
    @SerialName("slug")
    val slug: String? = null,
    @SerialName("socialMediaTags")
    val socialMediaTags: LinkSocialMediaTagsModel? = null
) {

    @Serializable
    data class LinkAnalyticsTagsModel(
        @SerialName("campaign")
        val campaign: String? = null,
        @SerialName("channel")
        val channel: String? = null,
        @SerialName("feature")
        val feature: String? = null,
        @SerialName("tags")
        val tags: String? = null
    )

    @Serializable
    data class LinkRedirectsModel(
        @SerialName("androidRedirect")
        val androidRedirect: String? = null,
        @SerialName("desktopRedirect")
        val desktopRedirect: String? = null,
        @SerialName("iosRedirect")
        val iosRedirect: String? = null,
        @SerialName("webOnly")
        val webOnly: Boolean? = null
    )

    @Serializable
    data class LinkSocialMediaTagsModel(
        @SerialName("description")
        val description: String? = null,
        @SerialName("title")
        val title: String? = null
    )
}