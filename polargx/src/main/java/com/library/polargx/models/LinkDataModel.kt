package com.library.polargx.models

import DictionaryModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LinkDataModel(
    @SerialName("analyticsTags")
    val analyticsTags: DictionaryModel?,
    @SerialName("socialMediaTags")
    val socialMediaTags: DictionaryModel?,
    @SerialName("data")
    val data: DictionaryModel?,
    @SerialName("slug")
    val slug: String?,
    @SerialName("url")
    val url: String?
)