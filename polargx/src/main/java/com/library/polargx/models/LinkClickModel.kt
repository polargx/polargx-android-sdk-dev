package com.library.polargx.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
class LinkClickModel(
    @SerialName("sdkUsed")
    val sdkUsed: Boolean?,
    @SerialName("unid")
    val unid: String?,
    @SerialName("url")
    val url: String?
) : Parcelable
