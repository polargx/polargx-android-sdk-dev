package com.library.polargx.repository.user.local

import android.content.SharedPreferences

class UserLocalDatasourceImpl(
    private val sf: SharedPreferences
) : UserLocalDatasource
