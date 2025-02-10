package com.app.main.splash

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.library.core.activity.BaseActivityViewModel
import com.library.core.activity.splash.BaseSplashActivity
import com.library.core.extension.viewBinding
import com.library.link_attribution.LinkAttribution
import com.library.link_attribution.listener.LinkInitListener
import com.linkattribution.sample.R
import com.linkattribution.sample.databinding.ActivitySplashBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SplashActivity : BaseSplashActivity(R.layout.activity_splash),
    SplashActivityViewModel.LiveEvent {

    private val mViewModel: SplashActivityViewModel by viewModel()
    private val binding by viewBinding(ActivitySplashBinding::bind)

    override fun getViewModel(): BaseActivityViewModel<out BaseActivityViewModel.LiveEvent> {
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mViewModel.mUiEvent.setEventReceiver(this, this)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        initData()

//        if (isLocalNotification(intent)) {
//            mViewModel.handleNotification(intent)
//        }
    }

    private fun initData() {
        mViewModel.loadInitData(intent)
    }

    override fun onStart() {
        super.onStart()
//        Branch.sessionBuilder(this)
//            .withCallback(mViewModel.application.getPresenter().getBranchIOListener())
//            .withData(this.intent?.data)
//            .withDelay(2700)
//            .init()

        LinkAttribution.init(
            activity = this,
            uri = intent?.data,
            listener = object : LinkInitListener {
                override fun onInitFinished(attributes: Map<String?, String?>?, error: Throwable?) {
                    mViewModel.onStartLinkAttributionInitFinished(attributes, error)
                }
            })
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        LinkAttribution.reInit(
            activity = this,
            uri = intent.data,
            listener = object : LinkInitListener {
                override fun onInitFinished(attributes: Map<String?, String?>?, error: Throwable?) {
                    mViewModel.onNewIntentAttributionInitFinished(attributes, error)
                }
            })
//        if (intent != null &&
//            intent.hasExtra("branch_force_new_session") &&
//            intent.getBooleanExtra("branch_force_new_session", false)
//        ) {
//            Branch.sessionBuilder(this)
//                .withCallback(mViewModel.application.getPresenter().getBranchIOListener())
//                .reInit()
//        }
    }

}