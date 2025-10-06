package com.library.polargx.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LinkDataModel(
    @SerialName("analyticsTags")
    val analyticsTags: DictionaryModel? = null,
    @SerialName("socialMediaTags")
    val socialMediaTags: DictionaryModel? = null,
    @SerialName("data")
    val data: DictionaryModel? = null,
    @SerialName("slug")
    val slug: String? = null,
    @SerialName("url")
    val url: String? = null
)