package com.app.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.library.polar_gx.PolarGX
import com.library.polar_gx.listener.LinkInitListener
import com.polargx.sample.R
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity(R.layout.activity_main),
    MainActivityViewModel.LiveEvent {

    private val mViewModel: MainActivityViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        PolarGX.init(
            activity = this,
            uri = intent?.data,
            listener = object : LinkInitListener {
                override fun onInitFinished(attributes: Map<String, String?>?, error: Throwable?) {
                    mViewModel.onStartPolarGXInitFinished(attributes, error)
                }
            })
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        PolarGX.reInit(
            activity = this,
            uri = intent.data,
            listener = object : LinkInitListener {
                override fun onInitFinished(attributes: Map<String, String?>?, error: Throwable?) {
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