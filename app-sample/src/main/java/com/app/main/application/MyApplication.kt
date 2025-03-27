package com.app.main.application

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.multidex.MultiDex
import com.app.main.di.appModule
import com.library.polargx.PolarApp
import com.polargx.sample.BuildConfig
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MyApplication : Application() {

    companion object {
        const val TAG = "Application"
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@MyApplication)
            modules(appModule)
        }

        initData()
        initListener()
    }

    private fun initData() {
        PolarApp.isDevelopmentEnabled = true
        PolarApp.isLoggingEnabled = true

        PolarApp.initialize(
            application = this,
//            appId = BuildConfig.POLAR_APP_ID,
//            apiKey = BuildConfig.POLAR_API_KEY,
            appId = "ad71f83f-4bc3-447a-94c8-d78c3ec8cce2",
            apiKey = "IF7M2WBS034QFvycReBBPavHfo5xXOkLOaOJmP9i",
            onLinkClickHandler = { link, data, error ->
                Log.d(TAG, "\n[DEMO] detect clicked: $link, data: $data, error: $error\n")
            },
            onInitFinished = {
                PolarApp.updateUser(
                    userID = "e1a3cb25-839e-4deb-95b0-2fb8ebd79401",
                    attributes = mapOf(
                        "name" to "a",
                        "email" to "a@gmail.com",
                    )
                )
                PolarApp.updateUser(
                    userID = "e1a3cb25-839e-4deb-95b0-2fb8ebd79402",
                    attributes = mapOf(
                        "name" to "b",
                        "email" to "b@gmail.com",
                    )
                )
            }
        )
    }

    private fun initListener() {

    }
}