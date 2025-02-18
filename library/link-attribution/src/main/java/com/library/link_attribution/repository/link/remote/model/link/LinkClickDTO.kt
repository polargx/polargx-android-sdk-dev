package com.library.link_attribution.repository.link.remote.model.link

import android.os.Parcelable
import com.library.link_attribution.repository.link.model.link.LinkClickModel
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
class LinkClickDTO(
    @SerialName("sdkUsed")
    val sdkUsed: Boolean? = null,
    @SerialName("unid")
    val unid: String? = null,
    @SerialName("url")
    val url: String? = null
) : Parcelable {

    fun toExternal(): LinkClickModel {
        return LinkClickModel(
            sdkUsed = sdkUsed,
            unid = unid,
            url = url
        )
    }

}
