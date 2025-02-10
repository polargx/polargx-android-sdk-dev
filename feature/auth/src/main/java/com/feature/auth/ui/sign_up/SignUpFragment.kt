package com.feature.auth.ui.sign_up

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.app.shared.AppConstants
import com.feature.auth.R
import com.feature.auth.databinding.FragmentSignUpBinding
import com.library.core.extension.registerBroadcastReceiver
import com.library.core.extension.viewBinding
import com.library.core.fragment.BaseFragment
import com.library.core.fragment.BaseFragmentViewModel
import com.library.core.text.setDirection
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SignUpFragment : BaseFragment(R.layout.fragment_sign_up),
    SignUpFragmentViewModel.LiveEvent {

    private val mViewModel: SignUpFragmentViewModel by viewModel()
    private val binding by viewBinding(FragmentSignUpBinding::bind)

    private var mBroadcastReceiver: BroadcastReceiver? = null

    init {
        mName = "SignUpFragment"
    }

    private var mListener: Listener? = null

    fun setListener(listener: Listener?): SignUpFragment {
        mListener = listener
        return this
    }

    companion object {
        fun newInstance(): SignUpFragment {
            val args = Bundle()
            val fragment = SignUpFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun getViewModel(): BaseFragmentViewModel<out SignUpFragmentViewModel.LiveEvent> {
        return mViewModel
    }

    override fun getRootView(): View {
        return binding.root
    }

    override fun init(view: View) {
        super.init(view)

        mViewModel.mUiEvent.setEventReceiver(this, this)

        lifecycleScope.launch {
            mViewModel.uiState.collect { uiState ->
                binding.root.setDirection(
                    direction = uiState.nativeLanguageDirection
                )
            }
        }

        initData()
        initListener()
    }

    private fun initData() {
        mViewModel.loadInitData()
    }

    private fun initListener() {
        if (mBroadcastReceiver == null) {
            mBroadcastReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    mViewModel.onBroadcastReceive(context, intent)
                }
            }
        }
        context?.registerBroadcastReceiver(
            mBroadcastReceiver,
            IntentFilter(AppConstants.Broadcast.APP_RESOURCES_CHANGED_ACTION)
        )
    }

    override fun onDestroy() {
        mBroadcastReceiver?.let {
            context?.unregisterReceiver(it)
            mBroadcastReceiver = null
        }
        super.onDestroy()
    }

    interface Listener {
    }
}