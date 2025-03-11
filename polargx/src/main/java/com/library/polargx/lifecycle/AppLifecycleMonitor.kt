package com.library.polargx.lifecycle

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.library.polargx.logger.PolarLogger

class AppLifecycleMonitor(
    private val listener: Listener?
) : Application.ActivityLifecycleCallbacks {

    companion object {
        const val TAG = ">>>AppLifecycleMonitor"
    }

    val mCreatedActivityList: MutableList<Activity> by lazy { mutableListOf() }
    val mStartedActivityList: MutableList<Activity> by lazy { mutableListOf() }
    val mResumeActivityList: MutableList<Activity> by lazy { mutableListOf() }
    var isAppInBackground: Boolean = false
    var isBackAllActivity: Boolean = false
    var mLastActivity: Activity? = null

    init {
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
            fun onAppBackgrounded() {
                listener?.onAppBackgrounded()
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_START)
            fun onAppForegrounded() {
                listener?.onAppForegrounded()
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onAppDestroyed() {
                PolarLogger.d(TAG, "App is terminating")
            }

        })
    }


    fun isAppAlive(): Boolean {
        return mCreatedActivityList.isNotEmpty()
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        mLastActivity = activity
        if (mCreatedActivityList.isEmpty()
            && mStartedActivityList.isEmpty()
            && mResumeActivityList.isEmpty()
        ) {
            if (isBackAllActivity) {
                listener?.onAppOpenWhenApplicationAlive()
                isBackAllActivity = false
            }
            listener?.onAppFirstActivityCreated()
        }
        mCreatedActivityList.add(activity)
    }

    override fun onActivityStarted(activity: Activity) {
        mStartedActivityList.add(activity)
    }

    override fun onActivityResumed(activity: Activity) {
        mResumeActivityList.add(activity)
        if (isAppInBackground && mResumeActivityList.isNotEmpty()) {
            listener?.onAppGoToForeground()
            isAppInBackground = false
        }
    }

    override fun onActivityPaused(activity: Activity) {
        mResumeActivityList.remove(activity)
    }

    override fun onActivityStopped(activity: Activity) {
        mStartedActivityList.remove(activity)
        if (mStartedActivityList.isEmpty()) {
            isAppInBackground = true
            listener?.onAppGoToBackgroundViaHomeButton(mCreatedActivityList.lastOrNull())
        }
    }

    override fun onActivityDestroyed(activity: Activity) {
        mCreatedActivityList.remove(activity)
        if (mCreatedActivityList.isEmpty()
            && mStartedActivityList.isEmpty()
            && mResumeActivityList.isEmpty()
        ) {
            isAppInBackground = false
            isBackAllActivity = true
            listener?.onAppGoToBackgroundViaBackLastActivity(activity)
        }
        mLastActivity = mCreatedActivityList.lastOrNull()
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    interface Listener {
        fun onAppForegrounded()
        fun onAppBackgrounded()
        fun onAppFirstActivityCreated()
        fun onAppOpenWhenApplicationAlive()
        fun onAppGoToForeground()
        fun onAppGoToBackgroundViaHomeButton(foregroundActivity: Activity?)
        fun onAppGoToBackgroundViaBackLastActivity(lastActivity: Activity?)
    }
}