package com.feature.auth.ui.login

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.app.shared.AppConstants
import com.feature.auth.R
import com.feature.auth.databinding.FragmentLoginBinding
import com.library.core.extension.registerBroadcastReceiver
import com.library.core.extension.viewBinding
import com.library.core.fragment.BaseFragment
import com.library.core.fragment.BaseFragmentViewModel
import com.library.core.text.setDirection
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : BaseFragment(R.layout.fragment_login),
    LoginFragmentViewModel.LiveEvent {

    private val mViewModel: LoginFragmentViewModel by viewModel()
    private val binding by viewBinding(FragmentLoginBinding::bind)

    private var mBroadcastReceiver: BroadcastReceiver? = null

    init {
        mName = "LoginFragment"
    }

    private var mListener: Listener? = null

    fun setListener(listener: Listener?): LoginFragment {
        mListener = listener
        return this
    }

    companion object {
        fun newInstance(): LoginFragment {
            val args = Bundle()
            val fragment = LoginFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun getViewModel(): BaseFragmentViewModel<out LoginFragmentViewModel.LiveEvent> {
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