package com.library.polargx.extension

import android.content.Context
import android.net.ConnectivityManager
import java.net.Inet4Address
import java.net.NetworkInterface

fun Context?.getIP4Address(): String? {
    val interfaces = NetworkInterface.getNetworkInterfaces()
    while (interfaces.hasMoreElements()) {
        val networkInterface = interfaces.nextElement()
        for (inetAddress in networkInterface.inetAddresses) {
            if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                return inetAddress.hostAddress
            }
        }
    }
    return null
}

fun Context.getIP6Address(): String? {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
    val network = connectivityManager?.activeNetwork
    val linkProperties = connectivityManager?.getLinkProperties(network)
    return linkProperties?.linkAddresses?.firstOrNull()?.address?.hostAddress
}