package com.data.users

import com.data.users.model.UserModel
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun onAppDied()
    fun onLoggedOut()
    fun onTokenExpired()

    fun isLoggedIn(): Boolean
    fun isOnboardingCompleted(): Boolean

    fun getCacheToken(): String?
    fun setCacheToken(token: String?)

    fun getCacheProfile(): UserModel?
    fun setProfile(profile: UserModel?)
    fun fetchAndCacheProfile(): Flow<UserModel?>

}
