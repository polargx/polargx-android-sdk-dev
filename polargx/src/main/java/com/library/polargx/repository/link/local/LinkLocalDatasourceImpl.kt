package com.library.polargx.repository.link.local

import android.content.SharedPreferences
import com.library.polargx.Constants
import com.library.polargx.repository.link.local.model.link.LinkDataEntity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class LinkLocalDatasourceImpl(
    private val sf: SharedPreferences
) : LinkLocalDatasource {

    override fun getLinkData(): LinkDataEntity? {
        try {
            val jsonStr = sf.getString(
                Constants.Local.Prefers.Link.LINK_DATA_KEY,
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
                Constants.Local.Prefers.Link.LINK_DATA_KEY,
                jsonStr
            ).apply()
    }

}
