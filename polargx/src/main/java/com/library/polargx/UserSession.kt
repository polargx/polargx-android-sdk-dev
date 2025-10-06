package com.library.polargx

import com.library.polargx.api.ApiService
import com.library.polargx.api.fcm_tokens.deregister.DeregisterFCMRequest
import com.library.polargx.api.fcm_tokens.register.RegisterFCMRequest
import com.library.polargx.api.update_user.UpdateUserRequest
import com.library.polargx.helpers.ApiError
import com.library.polargx.helpers.Logger
import com.library.polargx.models.DeregisterFCMModel
import com.library.polargx.models.TrackEventModel
import com.library.polargx.models.RegisterFCMModel
import com.library.polargx.models.UpdateUserModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File

/**
 * Purpose: create user if needed by calling UpdateUser api.
 * Manage events since userId to be set and send to backend.
 * One UserSession instance will be created for only one user (userID).
 */
data class UserSession(
    val organizationUnid: String,
    val userID: String,
    val trackingFileStorage: File
) : KoinComponent {

    private var isValid = true

    private var attributes = mapOf<String, Any?>()
    private var pendingRegisterPushToken: String? = null
    private var lastRegisteredFCMToken: String? = null

    private val trackingEventQueue by lazy { TrackingEventQueue(trackingFileStorage) }

    private val apiService by inject<ApiService>()

    companion object {
        const val TAG = ">>>Polar"
    }

    /**
     * Keep all user attributes for next sending. I don't make sure server supports to merging existing user attributes and the new attributes.
     */
    suspend fun setAttributes(attrs: Map<String, Any?>) {
        if (!isValid) return
        attributes += attrs
        startToUpdateUser()
    }

    suspend fun setPushToken(fcm: String?) {
        if (!isValid) return
        pendingRegisterPushToken = fcm
        startToRegisterPushToken()
    }

    fun invalidate() {
        if (!isValid) return
        isValid = false

        Logger.d(">>>Polar", "Invalidate user session: $userID")

        CoroutineScope(Dispatchers.IO).launch {
            startToDeregisterPushToken()
        }
    }

    /**
     * Sending user attributes and user id to backend. This API call will create an user if need. After successful, we need to make `trackingEventQueue` to be ready and sending events if needed.
     * Stop sending retrying process if server returns status code #403.
     * Retry when network connection issue, server returns status code #400.
     */
    private suspend fun startToUpdateUser() {
        var submitError: Exception? = null

        do {
            try {
                val user = UpdateUserModel(organizationUnid, userID, attributes)
                val request = UpdateUserRequest.from(user)
                apiService.updateUser(request)
            } catch (e: Exception) {
                if (e is ApiError && e.code == 403) {
                    Logger.d(TAG, "UpdateUser: ⛔⛔⛔ INVALID appId OR apiKey! ⛔⛔⛔")
                    submitError = null
                } else {
                    Logger.d(TAG, "UpdateUser: failed ⛔️ + retrying 🔁: $e")
                    delay(1000)
                    submitError = e
                }
            }
        } while (submitError != null)

        trackingEventQueue.setReady()
        trackingEventQueue.sendEventsIfNeeded()
    }

    /**
     * Stop sending retrying process if server returns status code #403
     * Retry when network connection issue, server returns status code #400
     */
    private suspend fun startToRegisterPushToken() {
        var submitError: Exception?

        do {
            try {
                val registeringPushToken = pendingRegisterPushToken
                if (registeringPushToken != null) {
                    val fcm = RegisterFCMModel(organizationUnid, userID, registeringPushToken)
                    val request = RegisterFCMRequest.from(fcm)
                    apiService.registerFCM(request)
                } else {
                    lastRegisteredFCMToken = null
                }

                if (registeringPushToken == pendingRegisterPushToken) {
                    pendingRegisterPushToken = null
                }

                submitError = null
            } catch (e: Exception) {
                if (e is ApiError && e.code == 403) {
                    Logger.d(TAG, "RegisterPushToken: ⛔⛔⛔ INVALID appId OR apiKey! ⛔⛔⛔")
                    submitError = null
                } else {
                    Logger.d(TAG, "RegisterPushToken: failed ⛔️ + retrying 🔁: $e")
                    delay(1000)
                    submitError = e
                }
            }
        } while (submitError != null)

        if (!isValid) {
            startToDeregisterPushToken()
        }
    }

    private suspend fun startToDeregisterPushToken() {
        var submitError: Exception? = null

        do {
            try {
                lastRegisteredFCMToken?.let { fcmToken ->
                    val fcm = DeregisterFCMModel(organizationUnid, userID, fcmToken)
                    val request = DeregisterFCMRequest.from(fcm)
                    apiService.deregisterFCM(request)
                    lastRegisteredFCMToken = null
                }
            } catch (e: Exception) {
                if (e is ApiError && e.code == 403) {
                    Logger.d(TAG, "DeregisterPushToken: ⛔⛔⛔ INVALID appId OR apiKey! ⛔⛔⛔")
                    submitError = null
                } else {
                    Logger.d(TAG, "DeregisterPushToken: failed ⛔️ + retrying 🔁: $e")
                    delay(1000)
                    submitError = e
                }
            }
        } while (submitError != null)
    }

    /**
     * Track event for user.
     */
    suspend fun trackEvents(events: List<UntrackedEvent>) {
        events.map { event ->
            val (name, date, attributes) = event
            trackingEventQueue.push(
                TrackEventModel(
                    organizationUnid = organizationUnid,
                    userID = userID,
                    eventName = name,
                    eventTime = date,
                    data = attributes
                )
            )
        }
        trackingEventQueue.sendEventsIfNeeded()
    }
}