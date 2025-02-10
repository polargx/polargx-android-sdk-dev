package com.library.core.animation.translate

import android.view.View

class BaseTranslateYAnimation(
    targetView: View?,
    fromValue: Float,
    toValue: Float
) : BaseTranslateAnimation(targetView, fromValue, toValue) {

    override
    fun applyTranslateTransformation(progress: Float) {
        getTargetView()?.y = progress
    }
}