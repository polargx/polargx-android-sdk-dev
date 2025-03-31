package com.library.polargx.api.track_link

import android.os.Parcelable
import com.library.polargx.models.LinkClickModel
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
class TrackLinkClickResponse(
    @SerialName("data")
    val data: Data?
) : Parcelable {

    @Parcelize
    @Serializable
    data class Data(
        @SerialName("linkClick")
        val linkClick: LinkClickModel?
    ) : Parcelable
}