package com.library.core.text

import android.view.View
import androidx.annotation.StringDef

@Retention(AnnotationRetention.SOURCE)
@StringDef(
    TextDirection.LEFT_TO_RIGHT,
    TextDirection.RIGHT_TO_LEFT,
)
annotation class TextDirection {
    companion object {
        const val LEFT_TO_RIGHT = "left-right"
        const val RIGHT_TO_LEFT = "right-left"
    }
}

fun View.setDirection(
    direction: String?,
    vararg ignoreView: View?,
) {
    when (direction) {
        TextDirection.LEFT_TO_RIGHT -> {
            this.textDirection = View.TEXT_DIRECTION_LTR
        }

        TextDirection.RIGHT_TO_LEFT -> {
            this.textDirection = View.TEXT_DIRECTION_RTL
        }

        else -> {}
    }
    ignoreView.forEach {
        it?.textDirection = View.TEXT_DIRECTION_LTR
    }
}