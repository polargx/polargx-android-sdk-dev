package com.feature.auth.ui.sign_up

import android.content.Context
import android.content.Intent
import com.app.shared.logger.DebugLogger
import com.data.users.UserRepository
import com.feature.auth.ui.landing.model.LandingUIModel
import com.library.core.application.BaseApplication
import com.library.core.fragment.BaseFragmentViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SignUpFragmentViewModel(
    private val application: BaseApplication,
    private val userRepository: UserRepository,
) : BaseFragmentViewModel<SignUpFragmentViewModel.LiveEvent>(application) {

    companion object {
        const val TAG = "SignUpFragmentViewModel"
    }

    private val _uiState by lazy { MutableStateFlow(LandingUIModel()) }
    val uiState = _uiState.asStateFlow()

    fun loadInitData() {

    }

    fun onBroadcastReceive(context: Context?, intent: Intent?) {
        DebugLogger.d(TAG, "onBroadcastReceive: action=${intent?.action}")
        when (intent?.action) {

        }
    }

    interface LiveEvent : BaseFragmentViewModel.LiveEvent {
    }
}