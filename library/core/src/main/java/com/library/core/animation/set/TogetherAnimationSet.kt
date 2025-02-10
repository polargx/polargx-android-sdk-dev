package com.library.core.animation.set

import android.view.View
import com.library.core.animation.BaseAnimation

class TogetherAnimationSet(
    targetView: View?,
    shareInterpolator: Boolean = false
) : BaseAnimationSet(targetView, shareInterpolator) {

    override
    fun calculateTotalDuration() {
        mTotalDuration += this.startOffset
        mLastPlayAnimation = getLastAnimPlay()
        mTotalDuration += if (mLastPlayAnimation == null) 0 else mLastPlayAnimation!!.getDurationIncludeOffset()
    }

    override
    fun getLastAnimPlay(): BaseAnimation? {
        return if (mAnimationList.isEmpty()) {
            null
        } else {
            var maxDurationIncludeOffsetAnim: BaseAnimation? = null
            for (animation in mAnimationList) {
                if (maxDurationIncludeOffsetAnim == null
                    || animation.getDurationIncludeOffset() > maxDurationIncludeOffsetAnim.getDurationIncludeOffset()
                ) {
                    maxDurationIncludeOffsetAnim = animation
                }
            }
            maxDurationIncludeOffsetAnim
        }
    }

    override
    fun start() {
        super.start()
        onAnimationSetStart()
        if (mAnimationList.isEmpty()) {
            onAnimationSetEnd()
        } else {
            mTargetView?.startAnimation(this)
        }
    }

    override
    fun cancel() {
        super.cancel()
        onAnimationSetCancel()
        for (animation in mAnimationList) {
            animation.cancel()
        }
    }

    override
    fun onAnimationStart(animation: BaseAnimation?) {
    }

    override
    fun onAnimationEnd(animation: BaseAnimation?) {
        if (mLastPlayAnimation === animation) {
            onAnimationSetEnd()
        }
    }

    override
    fun onAnimationCancel(animation: BaseAnimation?) {
        if (isCancelled()) {
            onAnimationSetCancel()
        }
    }
}