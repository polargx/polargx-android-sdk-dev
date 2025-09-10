package com.library.polargx.helpers

import android.content.Context
import android.os.Build
import android.webkit.WebView
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class FingerprintGenerator(private val context: Context) {

    private val webView by lazy {
        WebView(context).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
        }
    }

    suspend fun generateFingerprint(ip: String): String {
        val deviceType = "android"
        val webkitFingerprint = try {
            getWebKitFingerprint() ?: ""
        } catch (e: Exception) {
            ""
        }

        return "$deviceType#ip:$ip$webkitFingerprint"
    }

    private fun getWebKitVersion(): String {
        val userAgent = webView.settings.userAgentString ?: ""
        val prefix = "AppleWebKit/"
        val startIndex = userAgent.indexOf(prefix)
        if (startIndex != -1) {
            val substring = userAgent.substring(startIndex + prefix.length)
            val spaceIndex = substring.indexOf(" ")
            if (spaceIndex != -1) {
                return substring.substring(0, spaceIndex)
            }
        }
        return "_"
    }

    private suspend fun getWebKitFingerprint(): String? {
        val jsScript = """
            (() => {
                const timeZone = Intl.DateTimeFormat().resolvedOptions().timeZone;
                const locale = Intl.DateTimeFormat().resolvedOptions().locale;
                const language = navigator.language;
                const languages = navigator.languages?.join(',') || navigator.language;
                const platform = navigator.platform;
                const userAgent = navigator.userAgent;
                const webkitVersion = userAgent.match(/AppleWebKit\/([^\s]+)/)?.[1] || '';
                const devicePixelRatio = window.devicePixelRatio;
                return `#tz:${"$"}{timeZone}#locale:${"$"}{locale}#lang:${"$"}{language}#langs:${"$"}{languages}#platform:${"$"}{platform}#webkit:${"$"}{webkitVersion}#dpr:${"$"}{devicePixelRatio}`;
            })()
        """.trimIndent()

        return evaluateJavascript(jsScript)
    }

    private suspend fun getWebKitLocale(): String? {
        val jsScript = "Intl.DateTimeFormat().resolvedOptions().locale;"
        return evaluateJavascript(jsScript)
    }

    private fun getPlatform(): String {
        return when {
            context.resources.configuration.isScreenWideColorGamut -> {
                "Tablet"
            }

            else -> Build.MODEL ?: "Android"
        }
    }

    private suspend fun evaluateJavascript(script: String): String? =
        suspendCancellableCoroutine { cont ->
            webView.evaluateJavascript(script) { result ->
                if (result != null) {
                    // Kết quả JS trả về thường ở dạng chuỗi JSON (có dấu ngoặc kép)
                    val cleanResult = result.trim('"')
                    cont.resume(cleanResult)
                } else {
                    cont.resume(null)
                }
            }
        }
}
