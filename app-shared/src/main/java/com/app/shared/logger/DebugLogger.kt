package com.app.shared.logger

import android.util.Log
import com.app.shared.BuildConfig

object DebugLogger {
    fun d(tag: String?, message: String?) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, message ?: "")
        }
    }
}