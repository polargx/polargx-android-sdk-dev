package com.app.main.application

import com.library.core.application.BaseApplicationContract

interface MyApplicationContract {
    interface View : BaseApplicationContract.View {
    }

    interface Presenter : BaseApplicationContract.Presenter
}