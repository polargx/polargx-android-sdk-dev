package com.library.link_attribution.repository.link.local.model

import com.library.link_attribution.repository.link.local.model.LinkEntity.AnalyticsTags.Companion.toEntity
import com.library.link_attribution.repository.link.local.model.LinkEntity.SocialMediaPreview.Companion.toEntity
import com.library.link_attribution.repository.link.model.LinkModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LinkEntity(
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
) {

    @Serializable
    data class AnalyticsTags(
        @SerialName("feature")
        val feature: String? = null,
        @SerialName("channel")
        val channel: String? = null,
        @SerialName("campaign")
        val campaign: String? = null,
        @SerialName("tags")
        val tags: List<String>? = null,
    ) {
        fun toExternal(): LinkModel.AnalyticsTags {
            return LinkModel.AnalyticsTags(
                feature = feature,
                channel = channel,
                campaign = campaign,
                tags = tags
            )
        }

        companion object {
            fun LinkModel.AnalyticsTags.toEntity(): AnalyticsTags {
                return AnalyticsTags(
                    feature = feature,
                    channel = channel,
                    campaign = campaign,
                    tags = tags
                )
            }
        }
    }

    @Serializable
    data class SocialMediaPreview(
        @SerialName("title")
        val title: String? = null,
        @SerialName("description")
        val description: String? = null,
    ) {
        fun toExternal(): LinkModel.SocialMediaPreview {
            return LinkModel.SocialMediaPreview(
                title = title,
                description = description
            )
        }

        companion object {
            fun LinkModel.SocialMediaPreview.toEntity(): SocialMediaPreview {
                return SocialMediaPreview(
                    title = title,
                    description = description
                )
            }
        }
    }

    fun toExternal(): LinkModel {
        return LinkModel(
            unid = unid,
            createdAt = createdAt,
            updatedAt = updatedAt,
            name = name,
            description = description,
            path = path,
            analyticsTags = analyticsTags?.toExternal(),
            socialMediaPreview = socialMediaPreview?.toExternal(),
            attributes = attributes
        )
    }

    companion object {
        fun LinkModel.toEntity(): LinkEntity {
            return LinkEntity(
                unid = unid,
                createdAt = createdAt,
                updatedAt = updatedAt,
                name = name,
                description = description,
                path = path,
                analyticsTags = analyticsTags?.toEntity(),
                socialMediaPreview = socialMediaPreview?.toEntity(),
                attributes = attributes
            )
        }
    }
}