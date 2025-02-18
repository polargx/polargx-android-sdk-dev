package com.library.link_attribution.repository.link.local

import android.content.SharedPreferences
import com.library.link_attribution.LinkAttributionConstants
import com.library.link_attribution.repository.link.local.model.link.LinkDataEntity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class LinkLocalDatasourceImpl(
    private val sf: SharedPreferences
) : LinkLocalDatasource {

    override fun getLinkData(): LinkDataEntity? {
        try {
            val jsonStr = sf.getString(
                LinkAttributionConstants.Local.Prefers.Link.LINK_DATA_KEY,
                null
            ) ?: return null
            return Json.decodeFromString<LinkDataEntity>(jsonStr)
        } catch (ex: Exception) {
            return null
        }
    }

    override fun setLinkData(link: LinkDataEntity?) {
        val jsonStr = try {
            Json.encodeToString(link)
        } catch (ex: Throwable) {
            null
        }
        this.sf.edit()
            .putString(
                LinkAttributionConstants.Local.Prefers.Link.LINK_DATA_KEY,
                jsonStr
            ).apply()
    }

}
