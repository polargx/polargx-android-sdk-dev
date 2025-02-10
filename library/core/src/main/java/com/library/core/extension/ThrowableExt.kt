package com.library.core.extension

import android.content.Context
import com.library.core.R
import java.net.UnknownHostException

fun Throwable?.getDisplayMessage(context: Context?): String? {
    return when {
        this is UnknownHostException -> context?.getString(R.string.internet_not_available_error)
        !this?.message.isNullOrEmpty() -> this?.message
        else -> context?.getString(R.string.unknown_error)
    }
}