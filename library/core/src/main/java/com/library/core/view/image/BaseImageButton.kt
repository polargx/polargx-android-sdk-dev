package com.library.core.view.image

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageButton
import com.library.core.R

class BaseImageButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = androidx.appcompat.R.attr.imageButtonStyle
) : AppCompatImageButton(context, attrs, defStyleAttr) {

    companion object {
        const val TAG = "BaseImageButton"
    }

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.BaseImageButton)
        a.recycle()
    }
}