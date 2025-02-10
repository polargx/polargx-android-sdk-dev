package com.app.main.application

import android.app.Activity
import com.data.users.UserRepository

import com.library.core.application.BaseApplication
import com.library.core.application.BaseApplicationPresenter
import com.library.core.application.lifecycle.ApplicationLifecycle

class MyApplicationPresenter(
    override val application: BaseApplication,
    override val view: MyApplicationContract.View,
    private val userRepository: UserRepository,
) : BaseApplicationPresenter(
    application = application,
    view = view,
    userRepository = userRepository,
), MyApplicationContract.Presenter {

    companion object {
        const val TAG = ">>>B2CApplicationPresenter"
    }

    var isLoadInitDataFinished = false
    var isBranchInitFinished = false

    fun loadInitData() {

        initListener()
    }

    private fun initListener() {
        application.registerActivityLifecycleCallbacks(
            ApplicationLifecycle(listener = object : ApplicationLifecycle.Listener {
                override fun onAppFirstActivityCreated() {
                }

                override fun onAppOpenWhenApplicationAlive() {
                }

                override fun onAppGoToForeground() {
                }

                override fun onAppGoToBackgroundViaHomeButton(foregroundActivity: Activity?) {
                }

                override fun onAppGoToBackgroundViaBackLastActivity(lastActivity: Activity?) {
                }

            })
        )
    }
}