package com.library.core.activity.listener

import android.view.MotionEvent

interface OnDispatchTouchEventListener {
    fun onDispatchTouchEvent(ev: MotionEvent?): Boolean
}