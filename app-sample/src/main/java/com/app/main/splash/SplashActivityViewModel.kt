package com.app.main.splash

import android.content.Intent
import android.util.Log
import com.data.users.UserRepository
import com.library.core.activity.BaseActivityViewModel
import com.app.main.application.MyApplication
import com.app.main.splash.model.SplashUIModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SplashActivityViewModel(
    val application: MyApplication,
    val userRepository: UserRepository,
) : BaseActivityViewModel<SplashActivityViewModel.LiveEvent>(
    application = application,
    userRepository = userRepository,
) {

    companion object {
        const val TAG = ">>>SplashActivityViewModel"
    }

    private val _uiState by lazy { MutableStateFlow(SplashUIModel()) }
    val uiState = _uiState.asStateFlow()


    fun loadInitData(intent: Intent?) {

    }

    fun onStartLinkAttributionInitFinished(attributes: Map<String, String?>?, error: Throwable?) {
        Log.d(
            TAG,
            "onStartLinkAttributionInitFinished: attributes=$attributes, error=$error"
        )
    }

    fun onNewIntentAttributionInitFinished(attributes: Map<String, String?>?, error: Throwable?) {
        Log.d(
            TAG,
            "onNewIntentAttributionInitFinished: attributes=$attributes, error=$error"
        )
    }

    interface LiveEvent : BaseActivityViewModel.LiveEvent {
    }
}