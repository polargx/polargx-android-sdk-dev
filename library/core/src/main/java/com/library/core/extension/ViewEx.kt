package com.library.core.extension

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.library.core.animation.BaseAnimation
import com.library.core.animation.translate.BaseTranslateYAnimation

fun View.isShowing(): Boolean {
    return this.parent != null && this.isShown
}

fun View.getTopView(): View? {
    return if (this is ViewGroup) {
        this.getChildAt(this.childCount - 1)
    } else {
        null
    }
}

fun View.getParentViewGroup(): ViewGroup? {
    return if (parent is ViewGroup) {
        parent as ViewGroup
    } else {
        null
    }
}

fun View.getAbsoluteX(): Float {
    var x = this.x
    var parent = getParentViewGroup()
    while (parent != null) {
        x += parent.x
        parent = parent.getParentViewGroup()
    }
    return x
}

fun View.getAbsoluteY(): Float {
    var y = this.y
    var parent = getParentViewGroup()
    while (parent != null) {
        y += parent.y
        parent = parent.getParentViewGroup()
    }
    return y
}


fun View.slideUpView(listener: BaseAnimation.OnAnimationStartEndListener? = null) {
    BaseTranslateYAnimation(this, height.toFloat(), 0f).apply {
        interpolator = FastOutSlowInInterpolator()
        duration = 400
        setOnStartEndListener(listener ?: object : BaseAnimation.OnAnimationStartEndListener {
            override fun onAnimationStart(animation: BaseAnimation?) {
                this@slideUpView.alpha = 1f
                this@slideUpView.visibility = View.VISIBLE
            }

            override fun onAnimationEnd(animation: BaseAnimation?) {
            }

            override fun onAnimationCancel(animation: BaseAnimation?) {
            }
        })
        setReverse(false)
        this@slideUpView.startAnimation(this)
    }
}

fun View.slideDownView(listener: BaseAnimation.OnAnimationStartEndListener? = null) {
    BaseTranslateYAnimation(
        this,
        this.height.toFloat(),
        0f
    ).apply {
        interpolator = FastOutSlowInInterpolator()
        duration = 600
        setOnStartEndListener(listener ?: object : BaseAnimation.OnAnimationStartEndListener {
            override fun onAnimationStart(animation: BaseAnimation?) {
            }

            override fun onAnimationEnd(animation: BaseAnimation?) {
                getParentViewGroup()?.removeView(this@slideDownView)
            }

            override fun onAnimationCancel(animation: BaseAnimation?) {
                getParentViewGroup()?.removeView(this@slideDownView)
            }
        })
        setReverse(true)
        this@slideDownView.startAnimation(this)
    }
}

fun View.addToView(root: ViewGroup?): View {
    val lp = ConstraintLayout.LayoutParams(
        ConstraintLayout.LayoutParams.MATCH_PARENT,
        ConstraintLayout.LayoutParams.MATCH_PARENT
    )
    return addToView(root, lp)
}

fun View.addToView(root: ViewGroup?, lp: ViewGroup.LayoutParams): View {
    root?.addView(this, lp)
    return this
}


fun View.toBitmap(): Bitmap? {
    if (width == 0 || height == 0) {
        return null
    }
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    draw(canvas)
    return bitmap
}

fun RecyclerView.smoothSnapToPosition(position: Int, snapMode: Int = LinearSmoothScroller.SNAP_TO_START) {
    val smoothScroller = object : LinearSmoothScroller(this.context) {
        override fun getVerticalSnapPreference(): Int = snapMode
        override fun getHorizontalSnapPreference(): Int = snapMode
    }
    smoothScroller.targetPosition = position
    layoutManager?.startSmoothScroll(smoothScroller)
}