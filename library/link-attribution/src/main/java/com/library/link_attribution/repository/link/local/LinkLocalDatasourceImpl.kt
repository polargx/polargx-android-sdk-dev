package com.library.link_attribution.repository.link.local

import android.content.SharedPreferences
import com.library.link_attribution.LinkAttributionConstants
import com.library.link_attribution.repository.link.local.model.LinkEntity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class LinkLocalDatasourceImpl(
    private val sf: SharedPreferences
) : LinkLocalDatasource {

    override fun getLink(): LinkEntity? {
        return try {
            val jsonStr = sf.getString(LinkAttributionConstants.Local.Prefers.LINK_KEY, null)
            if (jsonStr == null) null else Json.decodeFromString<LinkEntity>(jsonStr)
        } catch (ex: Exception) {
            null
        }
    }

    override fun setLink(link: LinkEntity?) {
        val jsonStr = try {
            Json.encodeToString(link)
        } catch (ex: Throwable) {
            null
        }
        this.sf.edit().putString(LinkAttributionConstants.Local.Prefers.LINK_KEY, jsonStr).apply()
    }

}
