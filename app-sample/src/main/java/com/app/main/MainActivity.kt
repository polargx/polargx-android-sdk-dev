package com.app.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.library.polargx.PolarApp
import com.polargx.sample.R
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity(R.layout.activity_main),
    MainActivityViewModel.LiveEvent {

    private val mViewModel: MainActivityViewModel by viewModel()

    override fun onStart() {
        super.onStart()
        PolarApp.shared.bind(
            uri = intent?.data,
            listener = { attributes, error ->
                mViewModel.onStartPolarInitFinished(attributes, error)
            }
        )
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        PolarApp.shared.reBind(
            uri = intent.data,
            listener = { attributes, error ->
                mViewModel.onNewIntentPolarInitFinished(attributes, error)
            }
        )
    }
}