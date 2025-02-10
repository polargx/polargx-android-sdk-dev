package com.library.core.application

import android.app.Application
import com.app.shared.logger.DebugLogger
import com.data.users.UserRepository
import org.koin.android.ext.android.inject

abstract class BaseApplication : Application(),
    BaseApplicationContract.View {

    companion object {
        const val TAG = ">>>BaseApplication"
    }

    private val mUserRepository: UserRepository by inject()

    override fun onCreate() {
        DebugLogger.d(TAG, "onCreate")
        super.onCreate()
    }
}