package com.app.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.library.polargx.PolarApp
import com.polargx.sample.R

class MainActivity : AppCompatActivity(R.layout.activity_main),
    MainActivityViewModel.LiveEvent {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
    }

    private fun initData() {

    }

    override fun onStart() {
        super.onStart()
        PolarApp.shared.bind(intent?.data)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        PolarApp.shared.reBind(intent.data)
    }
}