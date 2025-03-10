package com.library.polar_gx.repository.link.remote.api.link

import android.os.Parcelable
import com.library.polar_gx.repository.link.remote.model.link.LinkDataDTO
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class GetLinkDataResponse(
    @SerialName("data")
    val data: Data? = null
) : Parcelable {

    @Parcelize
    @Serializable
    data class Data(
        @SerialName("sdkLinkData")
        val sdkLinkData: LinkDataDTO? = null
    ) : Parcelable
}