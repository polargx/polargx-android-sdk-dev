package com.library.polargx

import com.library.polargx.api.ApiService
import com.library.polargx.api.update_user.UpdateUserRequest
import com.library.polargx.helpers.ApiError
import com.library.polargx.helpers.Logger
import com.library.polargx.models.UpdateUserModel
import com.library.polargx.models.TrackEventModel
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
    private var attributes = mapOf<String, String>()

    private val trackingEventQueue by lazy { TrackingEventQueue(trackingFileStorage) }

    private val apiService by inject<ApiService>()

    companion object {
        const val TAG = ">>>Polar"
    }

    /**
     * Keep all user attributes for next sending. I don't make sure server supports to merging existing user attributes and the new attributes.
     */
    fun setAttributes(newAttributes: Map<String, String>) {
        CoroutineScope(Dispatchers.IO).launch {
            attributes += newAttributes
            startToUpdateUser()
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
                if (e is ApiError) {
                    if (e.code == 403) {
                        Logger.d(TAG, "UpdateUser: ⛔⛔⛔ INVALID appId OR apiKey! ⛔⛔⛔")
                        submitError = null
                    }
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
     * Track event for user.
     */
    suspend fun trackEvent(name: String?, date: String?, attributes: Map<String, String>?) {
        trackingEventQueue.push(
            TrackEventModel(
                organizationUnid = organizationUnid,
                userID = userID,
                eventName = name,
                eventTime = date,
                data = attributes
            )
        )
        trackingEventQueue.sendEventsIfNeeded()
    }
}