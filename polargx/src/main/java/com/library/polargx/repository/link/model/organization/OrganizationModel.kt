package com.library.polargx.repository.link.model.organization

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class OrganizationModel(
    @SerialName("name")
    val name: String?,
    @SerialName("unid")
    val unid: String?,
    @SerialName("requiredRedirects")
    val requiredRedirects: Map<String, String?>?,
    @SerialName("socialMediaPreview")
    val socialMediaPreview: Map<String, String?>?,
) : Parcelable