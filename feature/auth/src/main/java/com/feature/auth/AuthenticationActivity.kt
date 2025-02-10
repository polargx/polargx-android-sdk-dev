package com.feature.auth

import android.content.Intent
import android.os.Bundle
import com.feature.auth.databinding.ActivityAuthBinding
import com.feature.auth.ui.landing.LandingFragment
import com.feature.auth.ui.login.LoginFragment
import com.feature.auth.ui.sign_up.SignUpFragment
import com.feature.shared.AppActions
import com.library.core.activity.BaseActivityViewModel
import com.library.core.activity.splash.BaseSplashActivity
import com.library.core.extension.viewBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class AuthenticationActivity : BaseSplashActivity(R.layout.activity_auth),
    AuthenticationActivityViewModel.LiveEvent {

    private val mViewModel: AuthenticationActivityViewModel by viewModel()
    private val binding by viewBinding(ActivityAuthBinding::bind)

    override fun getViewModel(): BaseActivityViewModel<out AuthenticationActivityViewModel.LiveEvent> {
        return mViewModel
    }

    override fun getContainerId(): Int {
        return R.id.activityContainer
    }

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)

        mViewModel.mUiEvent.setEventReceiver(this, this)

        initData()
        initListener()
    }


    private fun initData() {
        mViewModel.loadInitData(intent)
    }

    private fun initListener() {

    }

    override fun showLandingScreen() {
        val fragment = LandingFragment.newInstance()
        pushFragment(fragment, fragment.getName(), false)
    }

    override fun showSignupScreen() {
        val fragment = SignUpFragment.newInstance()
        pushFragment(fragment, fragment.getName(), true)
    }

    override fun showLoginScreen() {
        val fragment = LoginFragment.newInstance()
        pushFragment(fragment, fragment.getName(), true)
    }

    override fun startMainFlow(isJustFinishedOnboarding: Boolean) {
        val intent = AppActions.openMainIntent(this)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    override fun startOnboardingFlow() {
        val intent = AppActions.openOnboardingIntent(this)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}