package com.app.main.application

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.multidex.MultiDex
import com.app.main.di.appModule
import com.library.polargx.Polar
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
        Polar.isDevelopmentEnabled = true
        Polar.isLoggingEnabled = true

        Polar.initialize(
            application = this,
            appId = BuildConfig.POLAR_APP_ID,
            apiKey = BuildConfig.POLAR_API_KEY,
            onLinkClickHandler = { link, data, error ->
                Log.d(TAG, "\n[DEMO] detect clicked: $link, data: $data, error: $error\n")
            },
        )
    }

    private fun initListener() {

    }
}