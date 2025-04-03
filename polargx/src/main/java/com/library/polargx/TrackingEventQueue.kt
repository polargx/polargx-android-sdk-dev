package com.library.polargx

import android.util.Log
import com.library.polargx.api.ApiService
import com.library.polargx.helpers.ApiError
import com.library.polargx.helpers.Logger
import com.library.polargx.models.TrackEventModel
import com.library.polargx.api.track_event.TrackEventRequest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException

/**
 * Purpose: fetch events from disk and manage events.
 */
class TrackingEventQueue(val file: File) : KoinComponent {

    private val apiService by inject<ApiService>()

    var events = mutableListOf<TrackEventModel>()
        private set
    var isReady = false
        private set
    var isRunning = false
        private set

    companion object {
        const val TAG = ">>>Polar"
    }

    /**
     * Fetch unsent events from file.
     */
    init {
        try {
            val data = file.readText()
            events = Json.decodeFromString<MutableList<TrackEventModel>>(data)
        } catch (e: Exception) {
            events = mutableListOf()
        }
    }

    /**
     * Set isReady flag.
     * If isReady sets to True, Events will be saved to disk, The queue is ready to send data to backend.
     * If isReady sets to False, Events is not saved to the disk.
     */
    fun setReady() {
        val wasReady = isReady
        isReady = true

        if (!wasReady) {
            save()
        }
    }

    /**
     * Event still pushed to the queue if queue is not ready.
     */
    fun push(event: TrackEventModel) {
        events.add(event)
        save()
    }

    private fun willPop(): TrackEventModel? {
        return events.firstOrNull()
    }

    private fun pop() {
        if (events.isNotEmpty()) {
            events.removeAt(0)
            save()
        }
    }

    private fun save() {
        if (!isReady) return

        try {
            val data = Json.encodeToString(events)
            file.writeText(data)
        } catch (e: Exception) {
            error("??? $e")
        }
    }

    /**
     * Sending Event progress, Only one progress need to be ran at the time.
     */
    suspend fun sendEventsIfNeeded() {
        if (!isReady || isRunning) return

        isRunning = true

        while (true) {
            val event = willPop() ?: break

            try {
                val request = TrackEventRequest.from(event)
                apiService.trackEvent(request)
            } catch (e: UnknownHostException) {
                // Network error: stop sending, keep elements
                Logger.d(TAG, "Tracking: failed ⛔ + stopped ⛔: $e")
                break
            } catch (e: SocketTimeoutException) {
                // Network error: stop sending, keep elements
                Logger.d(TAG, "Tracking: failed ⛔ + stopped ⛔: $e")
                break
            } catch (e: ConnectException) {
                // Network error: stop sending, keep elements
                Logger.d(TAG, "Tracking: failed ⛔ + stopped ⛔: $e")
                break
            } catch (e: SSLHandshakeException) {
                // Network error: stop sending, keep elements
                Logger.d(TAG, "Tracking: failed ⛔ + stopped ⛔: $e")
                break
            } catch (e: ApiError) {
                // Server error: stop sending, keep elements saved in the disk
                val code = e.code ?: 0
                if (code >= 500) {
                    Logger.d(TAG, "Tracking: failed ⛔ + stopped ⛔: $e")
                    break
                }
            } catch (e: Exception) {
                // Server error: ignore element and send next one
                Logger.d(TAG, "Tracking: failed ⛔ + next 🔁: $e")
            }

            pop()
        }

        isRunning = false
    }
}