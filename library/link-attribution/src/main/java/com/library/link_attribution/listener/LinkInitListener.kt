package com.library.link_attribution.listener

interface LinkInitListener {
    fun onInitFinished(
        attributes: Map<String?, String?>?,
        error: Throwable?
    )
}