package com.library.core.animation.set

import android.view.View
import com.library.core.animation.BaseAnimation

class OrderedAnimationSet(
    targetView: View?,
    shareInterpolator: Boolean
) : BaseAnimationSet(targetView, shareInterpolator) {

    private var mAnimPlayingPosition = -1
    private var mCurrentPlayingMyAnimation: BaseAnimation? = null

    override
    fun calculateTotalDuration() {
        mTotalDuration += this.startOffset
        for (animation in mAnimationList) {
            mTotalDuration += animation.getDurationIncludeOffset()
        }
    }

    override
    fun getLastAnimPlay(): BaseAnimation? {
        return if (mAnimationList.isEmpty()) {
            null
        } else {
            mAnimationList[mAnimationList.size - 1]
        }
    }

    override
    fun start() {
        //reset values
        mAnimPlayingPosition = -1
        mCurrentPlayingMyAnimation = null
        //startForResult flow
        super.start()
        onAnimationSetStart()
        if (mAnimationList.isEmpty()) {
            onAnimationSetEnd()
        } else {
            mAnimPlayingPosition = 0
            playAnimAtPosition(mAnimPlayingPosition)
        }
    }

    private
    fun playAnimAtPosition(position: Int) {
        mCurrentPlayingMyAnimation = mAnimationList[position]
        mTargetView?.startAnimation(mCurrentPlayingMyAnimation)
    }

    override
    fun cancel() {
        super.cancel()
        mCurrentPlayingMyAnimation?.cancel()
    }

    override
    fun onAnimationStart(animation: BaseAnimation?) {
    }

    override
    fun onAnimationEnd(animation: BaseAnimation?) {
        if (mCurrentPlayingMyAnimation === mLastPlayAnimation) {
            mAnimPlayingPosition = -1
            onAnimationSetEnd()
        } else if (mCurrentPlayingMyAnimation === animation) {
            mAnimPlayingPosition++
            playAnimAtPosition(mAnimPlayingPosition)
        }
    }

    override
    fun onAnimationCancel(animation: BaseAnimation?) {
        if (isCancelled()) {
            onAnimationSetCancel()
        }
    }
}