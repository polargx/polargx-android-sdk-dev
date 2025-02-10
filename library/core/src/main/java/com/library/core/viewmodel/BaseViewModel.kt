package com.library.core.viewmodel

import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.app.shared.logger.DebugLogger
import com.library.core.event.BaseLiveEvent
import com.library.core.view.popup.OneOptionView

abstract class BaseViewModel<T : BaseViewModel.LiveEvent> : ViewModel() {

    companion object {
        const val TAG = ">>>BaseViewModel"
    }

    val mUiEvent by lazy { BaseLiveEvent<T>() }

    open fun getUiEvent(): BaseLiveEvent<T> {
        return mUiEvent
    }

    override fun onCleared() {
        DebugLogger.d(TAG, "onCleared: $this")
        super.onCleared()
    }

    interface LiveEvent {
        fun showLoading()
        fun hideLoading()

        fun showError(
            message: String?,
            action: String?,
            actionSelected: ((OneOptionView) -> Unit)?,
            backPressed: ((OneOptionView) -> Unit)?,
        )

        fun showToast(
            message: String?,
            duration: Int = Toast.LENGTH_SHORT
        )
    }
}