package com.library.core.animation

import android.view.View
import android.view.animation.Animation

abstract class BaseAnimation(
    private val mTargetView: View?,
    private var mFromValue: Float,
    private var mToValue: Float
) : Animation(), Animation.AnimationListener {

    private var isReverse = false
    private var isEnded = false
    private var isStarted = false
    private var isCancelled = false
    protected var mRangeValue = 0f
    protected var mStartValue = 0f

    private val mOnAnimationStartEndListenerList by lazy {
        ArrayList<OnAnimationStartEndListener>()
    }

    protected val mOnAnimationUpdateListenerList by lazy {
        ArrayList<OnAnimationUpdateListener>()
    }

    init {
        setReverse(false)
        setAnimationListener(this)
    }

    protected open fun getTargetView(): View? {
        return mTargetView
    }

    open fun isReverse(): Boolean {
        return isReverse
    }

    fun setReverse(reverse: Boolean) {
        isReverse = reverse
        setupAnimValues()
    }

    open fun isStarted(): Boolean {
        return isStarted
    }

    open fun setStarted(started: Boolean) {
        isStarted = started
    }

    open fun isEnded(): Boolean {
        return isEnded
    }

    open fun setEnded(ended: Boolean) {
        isEnded = ended
    }

    open fun getFromValue(): Float {
        return mFromValue
    }

    open fun setFromValue(fromValue: Float): BaseAnimation {
        mFromValue = fromValue
        setupAnimValues()
        return this
    }

    open fun getToValue(): Float {
        return mToValue
    }

    open fun setToValue(toValue: Float): BaseAnimation {
        mToValue = toValue
        setupAnimValues()
        return this
    }

    open fun setupAnimValues() {
        mRangeValue = if (isReverse()) -getRangeValue() else getRangeValue()
        mStartValue = if (isReverse()) mToValue else mFromValue
    }

    open fun getRangeValue(): Float {
        return mToValue - mFromValue
    }

    open fun getDurationIncludeOffset(): Long {
        return this.startOffset + this.duration
    }

    override
    fun cancel() {
        isCancelled = true
        super.cancel()
    }

    override
    fun onAnimationStart(animation: Animation?) {
        isStarted = true
        isEnded = false
        isCancelled = false
        for (listener in mOnAnimationStartEndListenerList) {
            listener.onAnimationStart(this)
        }
    }

    override
    fun onAnimationEnd(animation: Animation?) {
        isStarted = false
        isEnded = true
        for (listener in mOnAnimationStartEndListenerList) {
            if (isCancelled) {
                listener.onAnimationCancel(this)
            } else {
                listener.onAnimationEnd(this)
            }
        }
        isCancelled = false
    }

    override
    fun onAnimationRepeat(animation: Animation?) {

    }

    fun setOnStartEndListener(onAnimationStartEndListener: OnAnimationStartEndListener?) {
        onAnimationStartEndListener?.let {
            mOnAnimationStartEndListenerList.add(it)
        }
    }

    fun setOnAnimationUpdateListener(onAnimationUpdateListener: OnAnimationUpdateListener?) {
        onAnimationUpdateListener?.let {
            mOnAnimationUpdateListenerList.add(it)
        }
    }

    interface OnAnimationStartEndListener {
        fun onAnimationStart(animation: BaseAnimation?)
        fun onAnimationEnd(animation: BaseAnimation?)
        fun onAnimationCancel(animation: BaseAnimation?)
    }

    interface OnAnimationUpdateListener {
        fun onAnimationUpdate(
            animation: BaseAnimation?,
            targetView: View?,
            interpolatedTime: Float,
            progressValues: Float
        )
    }
}