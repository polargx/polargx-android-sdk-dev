package com.library.core.animation.scale

import android.view.View

class BaseScaleXAnimation(
    targetView: View?,
    fromValue: Float,
    toValue: Float
) : BaseScaleAnimation(targetView, fromValue, toValue) {

    override
    fun applyScaleTransformation(progress: Float) {
        getTargetView()?.scaleX = progress
    }
}