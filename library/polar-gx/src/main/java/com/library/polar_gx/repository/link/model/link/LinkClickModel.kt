package com.library.polar_gx.repository.link.model.link

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
class LinkClickModel(
    @SerialName("sdkUsed")
    val sdkUsed: Boolean? = null,
    @SerialName("unid")
    val unid: String? = null,
    @SerialName("url")
    val url: String? = null
) : Parcelable
