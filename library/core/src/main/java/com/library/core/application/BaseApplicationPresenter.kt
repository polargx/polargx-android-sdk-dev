package com.library.core.application

import com.data.users.UserRepository

abstract class BaseApplicationPresenter(
    open val application: BaseApplication,
    open val view: BaseApplicationContract.View,
    private val userRepository: UserRepository,
) : BaseApplicationContract.Presenter {

}