package com.library.polargx.listener

fun interface PolarInitListener {
    fun onInitFinished(
        attributes: Map<String, String?>?,
        error: Throwable?
    )
}