package com.library.core.application


interface BaseApplicationContract {
    interface View {
        fun getPresenter(): Presenter
    }

    interface Presenter {

    }
}