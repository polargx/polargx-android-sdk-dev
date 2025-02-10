package com.library.link_attribution.repository.tracking.local

import android.content.SharedPreferences
import com.library.link_attribution.LinkAttributionConstants
import com.library.link_attribution.repository.tracking.local.model.TrackClickEntity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class TrackingLocalDatasourceImpl(
    private val sf: SharedPreferences
) : TrackingLocalDatasource {

    override fun getTrackClick(): TrackClickEntity? {
        return try {
            val jsonStr = sf.getString(LinkAttributionConstants.Local.Prefers.TRACK_CLICK_KEY, null)
            if (jsonStr == null) null else Json.decodeFromString<TrackClickEntity>(jsonStr)
        } catch (ex: Exception) {
            null
        }
    }

    override fun setTrackClick(link: TrackClickEntity?) {
        val jsonStr = try {
            Json.encodeToString(link)
        } catch (ex: Throwable) {
            null
        }
        this.sf.edit().putString(LinkAttributionConstants.Local.Prefers.TRACK_CLICK_KEY, jsonStr)
            .apply()
    }

}
