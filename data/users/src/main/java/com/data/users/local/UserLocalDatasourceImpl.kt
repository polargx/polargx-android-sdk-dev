package com.data.users.local

import android.content.SharedPreferences
import com.data.shared.DataConstants
import com.data.users.local.model.UserEntity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class UserLocalDatasourceImpl(
    private val sf: SharedPreferences
) : UserLocalDatasource {

    override fun isLoggedIn(): Boolean {
        return getToken()?.isNotEmpty() == true
    }

    override fun getToken(): String? {
        return sf.getString(DataConstants.Local.Prefers.TOKEN_KEY, null)
    }

    override fun setToken(token: String?) {
        sf.edit().putString(DataConstants.Local.Prefers.TOKEN_KEY, token).apply()
    }

    override fun getProfile(): UserEntity? {
        return try {
            val jsonStr = sf.getString(DataConstants.Local.Prefers.PROFILE_KEY, null)
            if (jsonStr == null) null else Json.decodeFromString<UserEntity>(jsonStr)
        } catch (ex: Exception) {
            null
        }
    }

    override fun setProfile(user: UserEntity?) {
        val jsonStr = try {
            Json.encodeToString(user)
        } catch (ex: Throwable) {
            null
        }
        this.sf.edit().putString(DataConstants.Local.Prefers.PROFILE_KEY, jsonStr).apply()
    }

}
