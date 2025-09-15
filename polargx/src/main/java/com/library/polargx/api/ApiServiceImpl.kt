package com.library.polargx.api

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import com.library.polargx.Constants
import com.library.polargx.api.device_tokens.deregister.DeregisterDeviceTokenRequest
import com.library.polargx.api.device_tokens.register.RegisterDeviceTokenRequest
import com.library.polargx.api.fcm_tokens.deregister.DeregisterFCMRequest
import com.library.polargx.api.fcm_tokens.register.RegisterFCMRequest
import com.library.polargx.api.link_click.LinkClickResponse
import com.library.polargx.api.link_data.LinkDataResponse
import com.library.polargx.api.track_event.TrackEventRequest
import com.library.polargx.api.track_link.TrackLinkClickRequest
import com.library.polargx.api.track_link.TrackLinkClickResponse
import com.library.polargx.api.update_link.UpdateLinkClickRequest
import com.library.polargx.api.update_user.UpdateUserRequest
import com.library.polargx.helpers.ApiError
import com.library.polargx.models.LinkClickModel
import com.library.polargx.models.LinkDataModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import io.ktor.http.path
import java.util.concurrent.TimeUnit

class ApiServiceImpl(
    private val client: HttpClient,
    private val sf: SharedPreferences
) : ApiService {

    override suspend fun updateUser(request: UpdateUserRequest?) {
        val response = client.post {
            url.path("api/v1/users/profile")
            setBody(request)
        }
        if (response.status.isSuccess()) {
            return response.body()
        }
        throw ApiError(response.bodyAsText())
    }

    override suspend fun trackEvent(request: TrackEventRequest?) {
        val response = client.post {
            url.path("api/v1/events")
            setBody(request)
        }
        if (response.status.isSuccess()) {
            return response.body()
        }
        throw ApiError(response.bodyAsText())
    }

    override suspend fun registerFCM(request: RegisterFCMRequest?) {
        val response = client.post {
            url.path("api/v1/users/fcm-tokens/register")
            setBody(request)
        }
        if (response.status.isSuccess()) {
            return response.body()
        }
        throw ApiError(response.bodyAsText())
    }

    override suspend fun deregisterFCM(request: DeregisterFCMRequest?) {
        val response = client.post {
            url.path("api/v1/users/fcm-tokens/deregister")
            setBody(request)
        }
        if (response.status.isSuccess()) {
            return response.body()
        }
        throw ApiError(response.bodyAsText())
    }

    override suspend fun registerDeviceToken(request: RegisterDeviceTokenRequest?) {
        val response = client.post {
            url.path("api/v1/users/device-tokens/register")
            setBody(request)
        }
        if (response.status.isSuccess()) {
            return response.body()
        }
        throw ApiError(response.bodyAsText())
    }

    override suspend fun deregisterDeviceToken(request: DeregisterDeviceTokenRequest?) {
        val response = client.post {
            url.path("api/v1/users/device-tokens/deregister")
            setBody(request)
        }
        if (response.status.isSuccess()) {
            return response.body()
        }
        throw ApiError(response.bodyAsText())
    }

    override suspend fun getLinkData(domain: String?, slug: String?): LinkDataModel? {
        val response = client.get {
            url.path("api/v1/links/resolve")
            parameter("domain", domain)
            parameter("slug", slug)
        }
        if (response.status.isSuccess()) {
            val body = response.body<LinkDataResponse>()
            return body.data?.sdkLinkData
        }
        throw ApiError(response.bodyAsText())
    }

    override suspend fun trackLinkClick(request: TrackLinkClickRequest?): LinkClickModel? {
        val response = client.post {
            url.path("api/v1/links/clicks")
            setBody(request)
        }
        if (response.status.isSuccess()) {
            val body = response.body<TrackLinkClickResponse?>()
            return body?.data?.linkClick
        }
        throw ApiError(response.bodyAsText())
    }

    override suspend fun updateLinkClick(clickUnid: String?, request: UpdateLinkClickRequest?) {
        val response = client.put {
            url.path("api/v1/links/clicks/$clickUnid")
            setBody(request)
        }
        if (response.status.isSuccess()) {
            return response.body()
        }
        throw ApiError(response.bodyAsText())
    }

    override suspend fun matchLinkClick(fingerprint: String?): LinkClickModel? {
        val response = client.get {
            url.path("api/v1/links/clicks/match")
            parameter("fingerprint", fingerprint)
        }
        if (response.status.isSuccess()) {
            val body = response.body<LinkClickResponse?>()
            return body?.data?.linkClick
        }
        throw ApiError(response.bodyAsText())
    }

    override suspend fun isFirstTimeLaunch(context: Context?, nowInMillis: Long): Boolean {
        if (context == null) return false
        val firstTime = sf.getBoolean(Constants.Local.Prefers.FIRST_TIME_KEY, true)
        if (!firstTime) return false // Already marked as not first time

        try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            val installTimeMillis = packageInfo.firstInstallTime

            // Check if install time is stored. If not, store it.
            val storedInstallTime = sf.getLong(Constants.Local.Prefers.INSTALL_TIME_KEY, 0L)
            if (storedInstallTime == 0L) {
                sf.edit().putLong(Constants.Local.Prefers.INSTALL_TIME_KEY, installTimeMillis)
                    .apply()
            }

            val timeDifferenceMillis = nowInMillis - installTimeMillis
            val timeDifferenceSeconds = TimeUnit.MILLISECONDS.toSeconds(timeDifferenceMillis)

            // If it's a very recent install (adjust threshold), it's the first launch.
            if (timeDifferenceSeconds < 60) { // Adjust threshold as needed
                sf.edit().putBoolean(Constants.Local.Prefers.FIRST_TIME_KEY, false)
                    .apply() // Mark as not first time
                return true
            } else {
                //If the time difference is greater than the threshold, and the app was reinstalled, it is not the first time
                sf.edit().putBoolean(Constants.Local.Prefers.FIRST_TIME_KEY, false).apply()
                return false
            }

        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            return false // Handle error as not first time
        }
    }
}