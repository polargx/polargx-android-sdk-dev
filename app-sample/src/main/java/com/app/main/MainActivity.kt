package com.app.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.library.polargx.PolarApp
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

        //TODO: use PolarApp.getInstance...().bind...
        PolarApp.shared.bind(
            activity = this,
            uri = intent?.data,
            listener = { attributes, error ->
                mViewModel.onStartPolarInitFinished(attributes, error)
            }
        )
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        //TODO: use PolarApp.getInstance...().reBind...
        PolarApp.shared.reBind(
            activity = this,
            uri = intent.data,
            listener = { attributes, error ->
                mViewModel.onNewIntentPolarInitFinished(attributes, error)
            }
        )
    }
}