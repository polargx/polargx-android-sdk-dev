package com.library.core.view.popup

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.library.core.activity.listener.OnBackPressedListener
import com.library.core.databinding.LayoutLoadingBinding

class LoadingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr),
    OnBackPressedListener {

    private var binding = LayoutLoadingBinding.inflate(
        LayoutInflater.from(context),
        this,
        false
    )

    init {
        addView(binding.root)
    }

    override fun onBackPressed(): Boolean {
        return true
    }

}
