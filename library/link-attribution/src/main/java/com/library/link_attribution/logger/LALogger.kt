package com.library.link_attribution.logger

import android.util.Log
import com.library.link_attribution.LinkAttribution

object LALogger {
    fun d(tag: String?, message: String?) {
        if (LinkAttribution.isLoggingEnabled) {
            Log.d(tag, message ?: "")
        }
    }

    fun i(tag: String?, message: String?) {
        if (LinkAttribution.isLoggingEnabled) {
            Log.i(tag, message ?: "")
        }
    }

    fun e(tag: String?, message: String?) {
        if (LinkAttribution.isLoggingEnabled) {
            Log.e(tag, message ?: "")
        }
    }
}