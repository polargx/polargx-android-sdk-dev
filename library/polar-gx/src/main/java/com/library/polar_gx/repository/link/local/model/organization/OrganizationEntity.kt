package com.library.polar_gx.repository.link.local.model.organization

import android.os.Parcelable
import com.library.polar_gx.repository.link.model.organization.OrganizationModel
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class OrganizationEntity(
    @SerialName("name")
    val name: String?,
    @SerialName("unid")
    val unid: String?,
    @SerialName("requiredRedirects")
    val requiredRedirects: Map<String, String?>?,
    @SerialName("socialMediaPreview")
    val socialMediaPreview: Map<String, String?>?,
) : Parcelable {

    fun toExternal(): OrganizationModel {
        return OrganizationModel(
            name = name,
            unid = unid,
            requiredRedirects = requiredRedirects,
            socialMediaPreview = socialMediaPreview,
        )
    }

    companion object {
        fun OrganizationModel?.toEntity(): OrganizationEntity? {
            if (this == null) return null
            return OrganizationEntity(
                name = name,
                unid = unid,
                requiredRedirects = requiredRedirects,
                socialMediaPreview = socialMediaPreview,
            )
        }
    }
}