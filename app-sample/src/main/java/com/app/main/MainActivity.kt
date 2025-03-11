package com.app.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.library.polargx.Polar
import com.library.polargx.listener.LinkInitListener
import com.polargx.sample.R
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity(R.layout.activity_main),
    MainActivityViewModel.LiveEvent {

    private val mViewModel: MainActivityViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
    }

    private fun initData() {
        mViewModel.loadInitData(intent)
    }

    override fun onStart() {
        super.onStart()
        Polar.init(
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
        Polar.reInit(
            activity = this,
            uri = intent.data,
            listener = object : LinkInitListener {
                override fun onInitFinished(attributes: Map<String, String?>?, error: Throwable?) {
                    mViewModel.onNewIntentAttributionInitFinished(attributes, error)
                }
            }
        )
    }
}