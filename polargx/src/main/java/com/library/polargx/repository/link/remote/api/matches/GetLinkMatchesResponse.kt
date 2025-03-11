package com.library.polargx.repository.link.remote.api.matches

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class GetLinkMatchesResponse(
    @SerialName("data")
    val data: Data? = null
) : Parcelable {

    @Parcelize
    @Serializable
    data class Data(
        @SerialName("linkClick")
        val linkClick: LinkClick?,
    ) : Parcelable {

        @Parcelize
        @Serializable
        class LinkClick(
            @SerialName("sdkUsed")
            val sdkUsed: Boolean? = null,
            @SerialName("unid")
            val unid: String? = null,
            @SerialName("url")
            val url: String? = null
        ) : Parcelable
    }

}