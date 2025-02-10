package com.library.core.view.popup

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import com.library.core.activity.listener.OnBackPressedListener
import com.library.core.databinding.LayoutOneOptionBinding
import com.library.core.extension.getParentViewGroup
import com.library.core.view.layout.BaseConstraintLayout

class OneOptionView @JvmOverloads constructor(
    context: Context,
    private var title: String? = null,
    private var message: String? = null,
    private var action: String? = null,
    private var actionSelected: ((OneOptionView) -> Unit)? = null,
    private var backPressed: ((OneOptionView) -> Unit)? = null,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BaseConstraintLayout(context, attrs, defStyleAttr),
    View.OnClickListener,
    OnBackPressedListener {

    private val binding = LayoutOneOptionBinding.inflate(
        LayoutInflater.from(context),
        this,
        false
    )

    init {
        addView(binding.root)
        initData()
        initListener()
    }

    fun setTitle(title: String?): OneOptionView {
        this.title = title
        binding.tvTitle.text = title
        binding.tvTitle.isVisible = !this.title.isNullOrEmpty()
        return this
    }

    fun setMessage(message: String?): OneOptionView {
        this.message = message
        binding.tvMessage.text = message
        return this
    }

    fun setAction(action: String?): OneOptionView {
        this.action = action
        binding.btnAction.text = action
        return this
    }

    private fun initData() {
        setTitle(title)
        setMessage(message)
        setAction(action)
    }

    private fun initListener() {
        binding.btnAction.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v) {
            binding.btnAction -> {
                if (actionSelected == null) {
                    getParentViewGroup()?.removeView(this)
                    return
                }
                actionSelected?.invoke(this)
            }
        }
    }


    override fun onBackPressed(): Boolean {
        if (backPressed == null) {
            getParentViewGroup()?.removeView(this)
            return true
        }
        backPressed?.invoke(this)
        return true
    }
}
