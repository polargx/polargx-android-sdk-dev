package com.library.link_attribution.repository.link.remote.model

import com.library.link_attribution.repository.link.model.PublicLinkDataModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PublicLinkDataDTO(
    @SerialName("analyticsTags")
    val analyticsTags: LinkAnalyticsTagsDTO? = null,
    @SerialName("redirects")
    val redirects: LinkRedirectsDTO? = null,
    @SerialName("slug")
    val slug: String? = null,
    @SerialName("socialMediaTags")
    val socialMediaTags: LinkSocialMediaTagsDTO? = null
) {

    @Serializable
    data class LinkAnalyticsTagsDTO(
        @SerialName("campaign")
        val campaign: String? = null,
        @SerialName("channel")
        val channel: String? = null,
        @SerialName("feature")
        val feature: String? = null,
        @SerialName("tags")
        val tags: String? = null
    ) {
        fun toExternal(): PublicLinkDataModel.LinkAnalyticsTagsModel {
            return PublicLinkDataModel.LinkAnalyticsTagsModel(
                campaign = campaign,
                channel = channel,
                feature = feature,
                tags = tags
            )
        }
    }

    @Serializable
    data class LinkRedirectsDTO(
        @SerialName("androidRedirect")
        val androidRedirect: String? = null,
        @SerialName("desktopRedirect")
        val desktopRedirect: String? = null,
        @SerialName("iosRedirect")
        val iosRedirect: String? = null,
        @SerialName("webOnly")
        val webOnly: Boolean? = null
    ) {
        fun toExternal(): PublicLinkDataModel.LinkRedirectsModel {
            return PublicLinkDataModel.LinkRedirectsModel(
                androidRedirect = androidRedirect,
                desktopRedirect = desktopRedirect,
                iosRedirect = iosRedirect,
                webOnly = webOnly
            )
        }
    }

    @Serializable
    data class LinkSocialMediaTagsDTO(
        @SerialName("description")
        val description: String? = null,
        @SerialName("title")
        val title: String? = null
    ) {
        fun toExternal(): PublicLinkDataModel.LinkSocialMediaTagsModel {
            return PublicLinkDataModel.LinkSocialMediaTagsModel(
                description = description,
                title = title
            )
        }
    }

    fun toExternal(): PublicLinkDataModel {
        return PublicLinkDataModel(
            analyticsTags = analyticsTags?.toExternal(),
            redirects = redirects?.toExternal(),
            slug = slug,
            socialMediaTags = socialMediaTags?.toExternal()
        )
    }
}