package com.app.main.application

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.app.main.di.appModule
import com.library.polar_gx.PolarGX
import com.lyft.kronos.AndroidClockFactory
import com.lyft.kronos.KronosClock
import com.polargx.sample.BuildConfig
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MyApplication : Application() {

    private lateinit var mKronosClock: KronosClock

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
        mKronosClock = AndroidClockFactory.createKronosClock(this)
        mKronosClock.syncInBackground()

        PolarGX.isLoggingEnabled = true
        PolarGX.initApp(
            application = this,
            appId = BuildConfig.BRANCH_APP_ID,
            apiKey = BuildConfig.BRANCH_API_KEY,
        )
    }

    private fun initListener() {

    }

    fun getInternetTimeInMillis(): Long {
        return mKronosClock.getCurrentTimeMs()
    }
}