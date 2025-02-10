package com.library.core.activity

import com.app.shared.logger.DebugLogger
import com.data.users.UserRepository
import com.library.core.application.BaseApplication
import com.library.core.viewmodel.BaseViewModel

abstract class BaseActivityViewModel<T : BaseActivityViewModel.LiveEvent>(
    private val application: BaseApplication,
    private val userRepository: UserRepository
) : BaseViewModel<T>() {

    companion object {
        const val TAG = "BaseActivityViewModel"
    }

    override fun onCleared() {
        DebugLogger.d(TAG, "onCleared: $this")
        super.onCleared()
    }

    fun loadInit() {

    }

    interface LiveEvent : BaseViewModel.LiveEvent
}