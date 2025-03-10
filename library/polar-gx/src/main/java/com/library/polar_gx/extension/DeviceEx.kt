package com.library.polar_gx.extension

import android.content.Context
import android.os.Build
import android.provider.Settings

fun Context?.getOsVersion(): String {
    return Build.VERSION.RELEASE
}

fun Context?.getSdkVersion(): Int {
    return Build.VERSION.SDK_INT
}

fun Context?.getDeviceModel(): String {
    return Build.MODEL
}

fun Context?.getManufacturer(): String {
    return Build.MANUFACTURER
}

fun Context?.getDeviceName(): String {
    return Settings.Secure.getString(
        this?.contentResolver,
        "bluetooth_name"
    )
}