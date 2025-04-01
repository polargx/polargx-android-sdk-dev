package com.library.polargx.listener

fun interface LinkInitListener {
    fun onInitFinished(
        attributes: Map<String, String>?,
        error: Throwable?
    )
}