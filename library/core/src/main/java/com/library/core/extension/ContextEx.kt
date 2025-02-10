package com.library.core.extension

import android.app.Activity
import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.graphics.Typeface
import android.media.AudioManager
import android.os.Build
import android.util.DisplayMetrics
import android.widget.Toast
import androidx.annotation.FontRes
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.library.core.typeface.CustomTypefaceSpan


fun Context.getScreenWidth(): Int {
    val displayMetrics = DisplayMetrics()
    (this as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
    return displayMetrics.widthPixels
}

fun Context.getVersionName(): String? {
    return packageManager?.getPackageInfo(packageName, 0)?.versionName
}

fun Context?.showToast(message: String?, duration: Int) {
    if (this == null) return
    Toast.makeText(this, message, duration).show()
}

fun Context.getApplicationName(): String {
    val applicationInfo = applicationInfo
    val stringId = applicationInfo.labelRes
    return if (stringId == 0) applicationInfo.nonLocalizedLabel.toString() else getString(stringId)
}

fun Context.getScreenHeight(): Int {
    val displayMetrics = DisplayMetrics()
    (this as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
    return displayMetrics.heightPixels
}

fun Context.getFileAuthority(): String {
    return "${this.packageName}.provider"
}

fun Context.getFontSpan(@FontRes id: Int): CustomTypefaceSpan {
    return CustomTypefaceSpan(newType = ResourcesCompat.getFont(this, id))
}

fun Context?.getFont(@FontRes id: Int?): Typeface? {
    if (this == null || id == null) return null
    return ResourcesCompat.getFont(this, id)
}

fun Context?.getDimension(id: Int?): Float? {
    if (this == null || id == null) return null
    return resources.getDimension(id)
}

fun Context?.registerBroadcastReceiver(
    broadcastReceiver: BroadcastReceiver?,
    intentFilter: IntentFilter?
) {
    if (Build.VERSION.SDK_INT >= 33) {
        this?.registerReceiver(
            broadcastReceiver,
            intentFilter,
            Context.RECEIVER_EXPORTED
        )
    } else {
        this?.registerReceiver(
            broadcastReceiver,
            intentFilter
        )
    }
}

fun Context?.isScheduleExactAlarmEnabled(): Boolean {
    if (Build.VERSION.SDK_INT < 31) return true
    val alarmManager = this?.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
    return Build.VERSION.SDK_INT >= 31
            && alarmManager != null
            && alarmManager.canScheduleExactAlarms()
}

fun Context?.isPhonePlayingSound(): Boolean {
    val audioManager = this?.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
    // Check if any music or other audio is currently active
    return audioManager != null && audioManager.isMusicActive
}


fun Context?.getAudioVolume(): Int? {
    val audioManager = this?.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
    return audioManager?.getStreamVolume(AudioManager.STREAM_MUSIC)
}

fun Context?.isNotificationEnabled(): Boolean {
    if (this == null) return false
    return NotificationManagerCompat.from(this).areNotificationsEnabled()
}


fun Activity?.getNavigationBarHeight(): Int {
    val window = this?.window ?: return 0

    // Try using WindowInsetsCompat
    var navigationBarHeight = 0
    ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { _, insets ->
        val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        navigationBarHeight = systemBarsInsets.bottom
        insets
    }

    // Fallback to resources method
    if (navigationBarHeight == 0) {
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        navigationBarHeight = if (resourceId > 0) {
            resources.getDimensionPixelSize(resourceId)
        } else {
            0
        }
    }

    return navigationBarHeight
}
