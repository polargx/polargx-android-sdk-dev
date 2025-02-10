package com.feature.shared

import android.content.Context
import android.content.Intent

object AppActions {

    fun openAuthIntent(context: Context?) = internalIntent(context, "auth.open")
    fun openOnboardingIntent(context: Context?) = internalIntent(context, "onboarding.open")
    fun openMainIntent(context: Context?) = internalIntent(context, "main.open")
    fun openSettingsIntent(context: Context?) = internalIntent(context, "settings.open")
    fun openSubscriptionIntent(context: Context?) =
        internalIntent(context, "subscription.subscribe.open")

    fun openWebViewIntent(context: Context?) = internalIntent(context, "web_view.url.open")

    private fun internalIntent(context: Context?, action: String) =
        Intent(action).setPackage(context?.packageName)
}