package com.library.polar_gx.repository.link.remote.api.organization

import android.os.Parcelable
import com.library.polar_gx.repository.link.remote.model.organization.OrganizationDTO
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class GetOrganizationResponse(
    @SerialName("data")
    val data: Data? = null
) : Parcelable {

    @Parcelize
    @Serializable
    data class Data(
        @SerialName("organization")
        val organization: OrganizationDTO? = null
    ) : Parcelable
}