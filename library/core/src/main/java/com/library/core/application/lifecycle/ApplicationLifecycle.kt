package com.library.core.application.lifecycle

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.library.core.activity.BaseActivity
import com.library.core.activity.splash.BaseSplashActivity

class ApplicationLifecycle(
    private val listener: Listener? = null,
) : Application.ActivityLifecycleCallbacks {

    val mCreatedActivityList: MutableList<Activity> by lazy { mutableListOf() }
    val mStartedActivityList: MutableList<Activity> by lazy { mutableListOf() }
    private val mResumeActivityList: MutableList<Activity> by lazy { mutableListOf() }
    var isAppInBackground: Boolean = false
    private var isBackAllActivity: Boolean = false
    var mLastActivity: Activity? = null

    fun isAppAlive(): Boolean {
        return mCreatedActivityList.isNotEmpty()
    }

    fun getForegroundActivity(): Activity? {
        return mCreatedActivityList.lastOrNull()
    }

    fun getForegroundActivityExceptSplash(): Activity? {
        return mCreatedActivityList.findLast { it !is BaseSplashActivity }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        mLastActivity = activity
        if (activity is BaseActivity) {
            if (mCreatedActivityList.isEmpty()
                && mStartedActivityList.isEmpty()
                && mResumeActivityList.isEmpty()
            ) {
                if (isBackAllActivity) {
                    listener?.onAppOpenWhenApplicationAlive()
                    isBackAllActivity = false
                }
                if (activity is BaseSplashActivity) {
                    listener?.onAppFirstActivityCreated()
                }
            }
            if (activity !is BaseSplashActivity) {
                mCreatedActivityList.add(activity)
            }
        }
    }

    override fun onActivityStarted(activity: Activity) {
        if (activity is BaseActivity && activity !is BaseSplashActivity) {
            mStartedActivityList.add(activity)
        }
    }

    override fun onActivityResumed(activity: Activity) {
        if (activity is BaseActivity && activity !is BaseSplashActivity) {
            mResumeActivityList.add(activity)
        }
        if (isAppInBackground && mResumeActivityList.isNotEmpty()) {
            listener?.onAppGoToForeground()
            isAppInBackground = false
        }
    }

    override fun onActivityPaused(activity: Activity) {
        if (mResumeActivityList.isNotEmpty() && activity is BaseActivity) {
            mResumeActivityList.remove(activity)
        }
    }

    override fun onActivityStopped(activity: Activity) {
        mStartedActivityList.remove(activity)
        if (activity is BaseActivity
            && !activity.isBackPressed()
            && mStartedActivityList.isEmpty()
        ) {
            isAppInBackground = true
            listener?.onAppGoToBackgroundViaHomeButton(getForegroundActivityExceptSplash())
        }
    }

    override fun onActivityDestroyed(activity: Activity) {
        if (activity is BaseActivity) {
            mCreatedActivityList.remove(activity)
            if (activity.isBackPressed()) {
                if (mCreatedActivityList.isEmpty()
                    && mStartedActivityList.isEmpty()
                    && mResumeActivityList.isEmpty()
                ) {
                    isAppInBackground = false
                    isBackAllActivity = true
                    listener?.onAppGoToBackgroundViaBackLastActivity(activity)
                }
            }
        }
        mLastActivity = mCreatedActivityList.lastOrNull()
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    interface Listener {
        fun onAppFirstActivityCreated()
        fun onAppOpenWhenApplicationAlive()
        fun onAppGoToForeground()
        fun onAppGoToBackgroundViaHomeButton(foregroundActivity: Activity?)
        fun onAppGoToBackgroundViaBackLastActivity(lastActivity: Activity?)
    }
}
