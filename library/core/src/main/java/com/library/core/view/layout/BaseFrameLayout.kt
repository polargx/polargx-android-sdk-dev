package com.library.core.view.layout

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.library.core.R

open class BaseFrameLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    companion object {
        const val TAG = "BaseFrameLayout"
    }

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.BaseFrameLayout)
        a.recycle()
    }
}