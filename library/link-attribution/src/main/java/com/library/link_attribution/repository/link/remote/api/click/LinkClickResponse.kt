package com.library.link_attribution.repository.link.remote.api.click

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
class LinkClickResponse(
    @SerialName("api_version")
    val apiVersion: Int? = null
) : Parcelable