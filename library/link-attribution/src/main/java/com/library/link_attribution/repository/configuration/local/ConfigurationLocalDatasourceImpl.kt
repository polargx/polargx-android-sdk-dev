package com.library.link_attribution.repository.configuration.local

import android.content.SharedPreferences
import com.library.link_attribution.LinkAttributionConstants
import com.library.link_attribution.repository.configuration.local.model.ConfigurationEntity
import com.library.link_attribution.repository.configuration.local.model.InitSessionEntity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ConfigurationLocalDatasourceImpl(
    private val sf: SharedPreferences
) : ConfigurationLocalDatasource {

    override fun getInitSession(): InitSessionEntity? {
        return try {
            val jsonStr =
                sf.getString(LinkAttributionConstants.Local.Prefers.INIT_SESSION_KEY, null)
            if (jsonStr == null) null else Json.decodeFromString<InitSessionEntity>(jsonStr)
        } catch (ex: Exception) {
            null
        }
    }

    override fun setInitSession(initSession: InitSessionEntity?) {
        val jsonStr = try {
            Json.encodeToString(initSession)
        } catch (ex: Throwable) {
            null
        }
        this.sf.edit().putString(LinkAttributionConstants.Local.Prefers.INIT_SESSION_KEY, jsonStr)
            .apply()
    }

    override fun getConfiguration(): ConfigurationEntity? {
        return try {
            val jsonStr =
                sf.getString(LinkAttributionConstants.Local.Prefers.INIT_SESSION_KEY, null)
            if (jsonStr == null) null else Json.decodeFromString<ConfigurationEntity>(jsonStr)
        } catch (ex: Exception) {
            null
        }
    }

    override fun setConfiguration(configuration: ConfigurationEntity?) {
        val jsonStr = try {
            Json.encodeToString(configuration)
        } catch (ex: Throwable) {
            null
        }
        this.sf.edit().putString(LinkAttributionConstants.Local.Prefers.CONFIGURATION_KEY, jsonStr)
            .apply()
    }

}
