package com.app.main.application

import android.app.Activity
import android.content.Context
import androidx.multidex.MultiDex
import com.app.shared.logger.DebugLogger
import com.library.core.application.BaseApplication
import com.library.core.application.BaseApplicationContract
import com.library.core.application.lifecycle.ApplicationLifecycle
import com.lyft.kronos.AndroidClockFactory
import com.lyft.kronos.KronosClock
import com.app.main.di.appModule
import com.library.link_attribution.LinkAttribution
import com.linkattribution.sample.BuildConfig
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MyApplication : BaseApplication() {

    private val mPresenter: MyApplicationContract.Presenter by inject()
    private lateinit var mKronosClock: KronosClock

    override fun getPresenter(): BaseApplicationContract.Presenter {
        return mPresenter
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        initData()
        initListener()

        onAppOpenWhenApplicationDied()
    }

    private fun initData() {
        startKoin {
            androidLogger()
            androidContext(this@MyApplication)
            modules(appModule)
        }
        mKronosClock = AndroidClockFactory.createKronosClock(this)
        mKronosClock.syncInBackground()

        LinkAttribution.isLoggingEnabled = true
        LinkAttribution.initApp(
            application = this,
            appId = BuildConfig.BRANCH_APP_ID,
            apiKey = BuildConfig.BRANCH_API_KEY,
        )
    }

    private fun initListener() {
        registerActivityLifecycleCallbacks(
            ApplicationLifecycle(listener = object : ApplicationLifecycle.Listener {
                override fun onAppFirstActivityCreated() {
                    DebugLogger.d(TAG, "onAppFirstActivityCreated: ")
                }

                override fun onAppOpenWhenApplicationAlive() {
                    DebugLogger.d(TAG, "onAppOpenWhenApplicationAlive: ")
                }

                override fun onAppGoToForeground() {
                    DebugLogger.d(TAG, "onAppGoToForeground: ")
                }

                override fun onAppGoToBackgroundViaHomeButton(foregroundActivity: Activity?) {
                    DebugLogger.d(
                        TAG,
                        "onAppGoToBackgroundViaHomeButton: foregroundActivity=$foregroundActivity"
                    )
                }

                override fun onAppGoToBackgroundViaBackLastActivity(lastActivity: Activity?) {
                    DebugLogger.d(
                        TAG,
                        "onAppGoToBackgroundViaBackLastActivity: lastActivity=$lastActivity"
                    )
                }
            })
        )
    }

    fun getInternetTimeInMillis(): Long {
        return mKronosClock.getCurrentTimeMs()
    }

    private fun onAppOpenWhenApplicationDied() {
        DebugLogger.d(TAG, "onAppOpenWhenApplicationDied: ")
    }

}