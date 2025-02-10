package com.feature.auth

import android.content.Intent
import com.data.users.UserRepository
import com.feature.auth.model.AuthenticationUIModel
import com.library.core.activity.BaseActivityViewModel
import com.library.core.application.BaseApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthenticationActivityViewModel(
    val application: BaseApplication,
    val userRepository: UserRepository,
) : BaseActivityViewModel<AuthenticationActivityViewModel.LiveEvent>(
    application = application,
    userRepository = userRepository,
) {

    companion object {
        const val TAG = ">>>AuthenticationActivityViewModel"
    }

    private val _uiState by lazy { MutableStateFlow(AuthenticationUIModel()) }
    val uiState = _uiState.asStateFlow()


    fun loadInitData(intent: Intent?) {

    }

    interface LiveEvent : BaseActivityViewModel.LiveEvent {
        fun showLandingScreen()
        fun showSignupScreen()
        fun showLoginScreen()
        fun startMainFlow(isJustFinishedOnboarding: Boolean)
        fun startOnboardingFlow()
    }
}