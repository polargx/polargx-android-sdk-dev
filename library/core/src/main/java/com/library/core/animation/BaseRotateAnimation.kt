package com.library.core.animation

import android.view.View
import android.view.animation.Transformation

class BaseRotateAnimation(
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
            getTargetView()?.alpha = progress
            for (updateListener in mOnAnimationUpdateListenerList) {
                updateListener.onAnimationUpdate(this, getTargetView(), interpolatedTime, progress)
            }
        }
    }
}