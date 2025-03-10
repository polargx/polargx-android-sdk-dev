package com.app.main

import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel

class MainActivityViewModel : ViewModel() {

    companion object {
        const val TAG = ">>>SplashActivityViewModel"
    }

    fun loadInitData(intent: Intent?) {

    }

    fun onStartPolarGXInitFinished(attributes: Map<String, String?>?, error: Throwable?) {
        Log.d(
            TAG,
            "onStartPolarGXInitFinished: attributes=$attributes, error=$error"
        )
    }

    fun onNewIntentAttributionInitFinished(attributes: Map<String, String?>?, error: Throwable?) {
        Log.d(
            TAG,
            "onNewIntentAttributionInitFinished: attributes=$attributes, error=$error"
        )
    }

    interface LiveEvent
}