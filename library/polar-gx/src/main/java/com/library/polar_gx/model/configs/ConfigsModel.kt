package com.library.polar_gx.model.configs

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
class ConfigsModel(
    @SerialName("appId")
    val appId: String?,
    @SerialName("apiKey")
    val apiKey: String?,
) : Parcelable
