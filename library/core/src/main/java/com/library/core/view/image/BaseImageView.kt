package com.library.core.view.image

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.library.core.R

open class BaseImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.BaseImageView)
        a.recycle()
    }
}