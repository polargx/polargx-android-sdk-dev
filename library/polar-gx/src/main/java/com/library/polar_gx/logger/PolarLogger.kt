package com.library.polar_gx.logger

import android.util.Log
import com.library.polar_gx.Polar

object PolarLogger {
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