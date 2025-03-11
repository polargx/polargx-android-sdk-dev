package com.library.polargx.listener

interface LinkInitListener {
    fun onInitFinished(
        attributes: Map<String, String?>?,
        error: Throwable?
    )
}