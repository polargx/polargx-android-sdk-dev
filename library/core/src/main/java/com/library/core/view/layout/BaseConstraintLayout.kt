package com.library.core.view.layout

import android.content.Context
import androidx.constraintlayout.widget.ConstraintLayout
import android.util.AttributeSet
import com.library.core.R

open class BaseConstraintLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr){

    companion object {
        const val TAG = "BaseConstraintLayout"
    }

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.BaseConstraintLayout)
        a.recycle()
    }
}