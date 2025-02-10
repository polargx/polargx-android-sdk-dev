package com.library.core.animation.scale

import android.view.View
import android.view.animation.Transformation
import com.library.core.animation.BaseAnimation

abstract class BaseScaleAnimation(
    targetView: View?,
    fromValue: Float,
    toValue: Float
) : BaseAnimation(targetView, fromValue, toValue) {

    override
    fun applyTransformation(
        interpolatedTime: Float,
        t: Transformation?
    ) {
        if (isStarted() && !isEnded()) {
            val progress = mStartValue + interpolatedTime * mRangeValue
            applyScaleTransformation(progress)
            for (updateListener in mOnAnimationUpdateListenerList) {
                updateListener.onAnimationUpdate(this, getTargetView(), interpolatedTime, progress)
            }
        }
    }

    abstract fun applyScaleTransformation(progress: Float)
}