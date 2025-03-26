package com.library.polargx

import com.library.polargx.logger.Logger
import com.library.polargx.repository.event.model.EventModel
import com.library.polargx.repository.user.UserRepository
import com.library.polargx.repository.user.model.UpdateUserModel
import com.library.polargx.repository.user.remote.api.UpdateUserRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File
import java.net.ConnectException
import java.net.UnknownHostException

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

    private val userRepository by inject<UserRepository>()

    companion object {
        const val TAG = "UserSession"
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
        var shouldRetry = false

        do {
            try {
                val user = UpdateUserModel(organizationUnid, userID, attributes)
                val request = UpdateUserRequest.from(user)
                val response = userRepository.updateUser(request)

                if (response.status.value == 403) {
                    Logger.d(TAG, "UpdateUser: ⛔⛔⛔ INVALID appId OR apiKey! ⛔⛔⛔")
                    shouldRetry = false
                }

                //TODO: how about http status code is 5xxm 4xx or 2xx? if status is 200 is success, otherwise throw error

            } catch (e: ConnectException) {
                // Handle connection refused or other connection issues (no internet)
                Logger.d(TAG, "UpdateUser: ⛔No internet connection + retry 🔁: $e")
                shouldRetry = true
                delay(1000)
            } catch (e: UnknownHostException) {
                // Handle DNS resolution failures (no internet or incorrect URL)
                Logger.d(TAG, "UpdateUser: ⛔Unknown host + retry 🔁: $e")
                shouldRetry = true
                delay(1000)
            } catch (e: Exception) {
                // Handle other exceptions (e.g., server errors, JSON parsing)
                Logger.d(TAG, "UpdateUser: ⛔⛔⛔An error occurred ⛔⛔⛔: $e")
                shouldRetry = false
            }
        } while (shouldRetry)

        trackingEventQueue.setReady()
        trackingEventQueue.sendEventsIfNeeded()
    }

    /**
     * Track event for user.
     */
    suspend fun trackEvent(name: String?, date: String?, attributes: Map<String, String>?) {
        trackingEventQueue.push(
            EventModel(
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