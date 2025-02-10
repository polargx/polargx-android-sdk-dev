package com.library.link_attribution.repository.link.remote.model

import com.library.link_attribution.repository.link.model.LinkModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LinkDTO(
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
}

//{
//    "data": {
//    "link": {
//    "unid": "932c8eaf-d2d5-4324-a2cb-41f283cf12cd",
//    "createdAt": "0001-01-01T00:00:00Z",
//    "updatedAt": "0001-01-01T00:00:00Z",
//    "name": "n-test01",
//    "description": "sample01",
//    "path": "test01",
//    "analyticsTags": {
//    "feature": "",
//    "channel": "",
//    "campaign": "",
//    "tags": null
//},
//    "socialMediaPreview": {
//    "title": "",
//    "description": ""
//},
//    "attributes": {
//    "": "",
//    "$invite_code": "12345"
//}
//}
//},
//    "api_version": 0
//}