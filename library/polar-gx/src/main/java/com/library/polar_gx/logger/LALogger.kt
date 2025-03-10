package com.library.polar_gx.logger

import android.util.Log
import com.library.polar_gx.PolarGX

object LALogger {
    fun d(tag: String?, message: String?) {
        if (PolarGX.isLoggingEnabled) {
            Log.d(tag, message ?: "")
        }
    }

    fun i(tag: String?, message: String?) {
        if (PolarGX.isLoggingEnabled) {
            Log.i(tag, message ?: "")
        }
    }

    fun e(tag: String?, message: String?) {
        if (PolarGX.isLoggingEnabled) {
            Log.e(tag, message ?: "")
        }
    }
}