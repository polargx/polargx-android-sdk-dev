package com.library.core.event

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LifecycleOwner

class BaseLiveEvent<T> : SingleLiveEvent<(T.() -> Unit)?>() {

    fun setEventReceiver(owner: LifecycleOwner, receiver: T) {
        observe(owner, { event ->
            if (event != null) {
                receiver.event()
            }
        })
    }

    fun sendEvent(event: (T.() -> Unit)?) {
        value = event
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun setEventReceiverForTesting(receiver: T) {
        observeForever { event ->
            if (event != null) {
                receiver.event()
            }
        }
    }

}