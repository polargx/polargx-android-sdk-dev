package com.library.core.animation

import android.view.animation.Transformation
import android.widget.ProgressBar

class ProgressBarAnimation(
    val targetView: ProgressBar?,
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
            targetView?.progress = progress.toInt()
            for (updateListener in mOnAnimationUpdateListenerList) {
                updateListener.onAnimationUpdate(this, getTargetView(), interpolatedTime, progress)
            }
        }
    }
}