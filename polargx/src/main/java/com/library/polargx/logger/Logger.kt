package com.library.polargx.logger

import android.util.Log
import com.library.polargx.Polar

object Logger {
    fun d(tag: String?, message: String?) {
        if (Polar.isLoggingEnabled) {
            Log.d(tag, message ?: "")
        }
    }

    fun i(tag: String?, message: String?) {
        if (Polar.isLoggingEnabled) {
            Log.i(tag, message ?: "")
        }
    }

    fun e(tag: String?, message: String?) {
        if (Polar.isLoggingEnabled) {
            Log.e(tag, message ?: "")
        }
    }
}