package com.library.link_attribution.logger

import android.util.Log
import com.library.link_attribution.BuildConfig

object DebugLogger {
    fun d(tag: String?, message: String?) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, message ?: "")
        }
    }
}