package com.library.core.fragment

import com.app.shared.logger.DebugLogger
import com.library.core.application.BaseApplication
import com.library.core.viewmodel.BaseViewModel

abstract class BaseFragmentViewModel<T : BaseFragmentViewModel.LiveEvent>(
    private val application: BaseApplication,
) : BaseViewModel<T>() {

    private var isInitialAnimStarted: Boolean? = null

    companion object {
        var TAG = "BaseFragmentViewModel"
    }

    override
    fun onCleared() {
        DebugLogger.d(TAG, "onCleared: $this")
        super.onCleared()
    }

    fun isInitAnimCompleted(): Boolean? {
        return isInitialAnimStarted
    }

    fun onInitAnimStarted() {
        isInitialAnimStarted = true
    }

    interface LiveEvent : BaseViewModel.LiveEvent {
    }
}