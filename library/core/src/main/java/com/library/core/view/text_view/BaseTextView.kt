package com.library.core.view.text_view

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.library.core.R

open class BaseTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.textViewStyle
) : AppCompatTextView(context, attrs, defStyleAttr) {

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.BaseTextView)
        a.recycle()
    }
}