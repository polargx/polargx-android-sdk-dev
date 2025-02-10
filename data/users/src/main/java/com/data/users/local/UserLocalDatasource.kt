package com.data.users.local

import com.data.users.local.model.UserEntity

interface UserLocalDatasource {

    fun isLoggedIn(): Boolean
    fun setToken(token: String?)
    fun getToken(): String?

    fun getProfile(): UserEntity?
    fun setProfile(user: UserEntity?)

}
