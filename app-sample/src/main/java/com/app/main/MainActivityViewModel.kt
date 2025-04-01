package com.app.main

import android.util.Log
import androidx.lifecycle.ViewModel

class MainActivityViewModel : ViewModel() {

    companion object {
        const val TAG = ">>>MainActivityViewModel"
    }

    fun onStartPolarInitFinished(attributes: Map<String, String>?, error: Throwable?) {
        Log.d(TAG, "onStartPolarInitFinished: attributes=$attributes, error=$error")
    }

    fun onNewIntentPolarInitFinished(attributes: Map<String, String>?, error: Throwable?) {
        Log.d(TAG, "onNewIntentPolarInitFinished: attributes=$attributes, error=$error")
    }

    interface LiveEvent
}