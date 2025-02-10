package com.library.core.extension

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

fun EditText.showKeyboard() {
    context.getSystemService(Context.INPUT_METHOD_SERVICE)?.let {
        if (it is InputMethodManager) {
            isFocusable = true
            isFocusableInTouchMode = true
            requestFocus()
            it.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
        }
    }
}

fun EditText.hideKeyboard() {
    context.getSystemService(Context.INPUT_METHOD_SERVICE)?.let {
        if (it is InputMethodManager) {
            it.hideSoftInputFromWindow(windowToken, 0)
        }
    }
}

