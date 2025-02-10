package com.library.core.animation.set

import android.view.View
import android.view.animation.AnimationSet
import com.library.core.animation.BaseAnimation

abstract class BaseAnimationSet(
    val mTargetView: View?,
    shareInterpolator: Boolean
) : AnimationSet(shareInterpolator),
    BaseAnimation.OnAnimationStartEndListener {

    private var isEnded = false
    private var isStarted = false
    private var isCancelled = false
    private var isReverse = false
    protected var mTotalDuration: Long = 0

    protected val mAnimationList: MutableList<BaseAnimation> by lazy {
        ArrayList<BaseAnimation>()
    }

    protected var mLastPlayAnimation: BaseAnimation? = null

    private var mOnAnimationSetStartEndListener: OnAnimationSetStartEndListener? = null


    protected abstract fun calculateTotalDuration()

    protected abstract fun getLastAnimPlay(): BaseAnimation?

    fun addAnimation(animation: BaseAnimation) {
        super.addAnimation(animation)
        animation.setOnStartEndListener(this)
        mAnimationList.add(animation)
    }

    override
    fun start() {
        calculateTotalDuration()
        mLastPlayAnimation = getLastAnimPlay()
    }

    override
    fun cancel() {
        isCancelled = true
    }

    fun onAnimationSetStart() {
        isStarted = true
        isEnded = false
        isCancelled = false
        mOnAnimationSetStartEndListener?.onAnimationSetStart(this)
    }

    fun onAnimationSetEnd() {
        isStarted = false
        isEnded = true
        isCancelled = false
        mOnAnimationSetStartEndListener?.onAnimationSetEnd(this)
    }

    fun onAnimationSetCancel() {
        isStarted = false
        isEnded = true
        mOnAnimationSetStartEndListener?.onAnimationSetCancel(this)
        isCancelled = false
    }

    fun setReverse(reverse: Boolean) {
        if (isReverse != reverse) {
            isReverse = reverse
            for (animation in mAnimationList) {
                animation.setReverse(!animation.isReverse())
            }
        }
    }

    fun isReverse(): Boolean {
        return isReverse
    }

    fun isCancelled(): Boolean {
        return isCancelled
    }

    fun getTotalDuration(): Long {
        return mTotalDuration
    }

    fun setOnAnimationStartEndListener(onAnimationSetStartEndListener: OnAnimationSetStartEndListener?) {
        this.mOnAnimationSetStartEndListener = onAnimationSetStartEndListener
    }

    interface OnAnimationSetStartEndListener {
        fun onAnimationSetStart(animationSet: BaseAnimationSet?)
        fun onAnimationSetEnd(animationSet: BaseAnimationSet?)
        fun onAnimationSetCancel(animationSet: BaseAnimationSet?)
    }
}