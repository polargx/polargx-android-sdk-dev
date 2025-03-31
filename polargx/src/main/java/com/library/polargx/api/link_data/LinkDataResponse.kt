package com.library.polargx.api.link_data

import android.os.Parcelable
import com.library.polargx.models.LinkDataModel
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class LinkDataResponse(
    @SerialName("data")
    val data: Data?
) : Parcelable {

    @Parcelize
    @Serializable
    data class Data(
        @SerialName("sdkLinkData")
        val sdkLinkData: LinkDataModel?
    ) : Parcelable
}