package com.library.polar_gx.listener

interface LinkInitListener {
    fun onInitFinished(
        attributes: Map<String, String?>?,
        error: Throwable?
    )
}