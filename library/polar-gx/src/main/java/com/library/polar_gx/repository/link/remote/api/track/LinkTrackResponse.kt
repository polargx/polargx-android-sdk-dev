package com.library.polar_gx.repository.link.remote.api.track

import android.os.Parcelable
import com.library.polar_gx.repository.link.remote.model.link.LinkClickDTO
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
class LinkTrackResponse(
    @SerialName("data")
    val data: Data? = null
) : Parcelable {

    @Parcelize
    @Serializable
    data class Data(
        @SerialName("linkClick")
        val linkClick: LinkClickDTO?,
    ) : Parcelable

}