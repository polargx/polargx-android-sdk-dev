package com.data.users

import com.data.shared.ApiError
import com.data.users.local.UserLocalDatasource
import com.data.users.local.model.UserEntity.Companion.toEntity
import com.data.users.model.UserModel
import com.data.users.remote.UserRemoteDatasource
import com.data.users.remote.api.GetUserProfileResponse
import io.ktor.client.call.body
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UserRepositoryImpl(
    private val localDatasource: UserLocalDatasource,
    private val remoteDatasource: UserRemoteDatasource
) : UserRepository {

    companion object {
        const val TAG = ">>>UserRepositoryImpl"
    }

    private var mToken: String? = null
    private var mProfile: UserModel? = null

    override fun onAppDied() {
        mProfile = null
    }

    private fun onUnauthenticated() {
        mProfile = null
    }

    override fun onLoggedOut() {
        onUnauthenticated()
    }

    override fun onTokenExpired() {
        onUnauthenticated()
    }

    override fun isLoggedIn(): Boolean {
        return localDatasource.isLoggedIn()
    }

    override fun isOnboardingCompleted(): Boolean {
        return mProfile?.onboardingComplete == true
    }

    override fun getCacheToken(): String? {
        if (mToken == null) {
            mToken = localDatasource.getToken()
        }
        return mToken
    }

    override fun setCacheToken(token: String?) {
        mToken = token
        localDatasource.setToken(token)
    }

    override fun getCacheProfile(): UserModel? {
        if (mProfile == null) {
            mProfile = localDatasource.getProfile()?.toExternal()
        }
        return mProfile
    }

    override fun setProfile(profile: UserModel?) {
        mProfile = profile
        localDatasource.setProfile(profile?.toEntity())
    }

    override fun fetchAndCacheProfile(): Flow<UserModel?> {
        return flow {
            val response = remoteDatasource.getProfile()
            if (response.status.isSuccess()) {
                val body = response.body<GetUserProfileResponse>()
                mProfile = body.data?.user?.toExternal()
                localDatasource.setProfile(mProfile?.toEntity())
                emit(mProfile)
            } else {
                throw ApiError(response.bodyAsText())
            }
        }
    }

}
