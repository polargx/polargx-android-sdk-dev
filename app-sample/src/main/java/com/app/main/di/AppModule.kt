package com.app.main.di

import android.content.Context
import com.app.main.application.MyApplication
import com.app.main.application.MyApplicationContract
import com.app.main.application.MyApplicationPresenter
import com.app.main.MainActivityViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    single { androidApplication() as? MyApplicationContract.View }
    single { androidApplication() as? MyApplication }
    single { androidApplication().getSharedPreferences("android_base1.file", Context.MODE_PRIVATE) }

    single<MyApplicationContract.Presenter> { MyApplicationPresenter() }

    viewModelOf(::MainActivityViewModel)
}