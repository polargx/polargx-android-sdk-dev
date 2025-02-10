package com.library.core.view.button

import android.content.Context
import androidx.appcompat.widget.AppCompatButton
import android.util.AttributeSet
import com.library.core.R

class BaseButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = androidx.appcompat.R.attr.buttonStyle
) : AppCompatButton(context, attrs, defStyleAttr) {

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.BaseButton)
        a.recycle()
    }
}