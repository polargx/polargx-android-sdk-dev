package com.library.polargx

import com.library.polargx.logger.Logger
import com.library.polargx.repository.event.EventRepository
import com.library.polargx.repository.event.model.EventModel
import com.library.polargx.repository.event.remote.api.EventTrackRequest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File
import java.net.URL
import java.net.UnknownHostException

/// Purpose: fetch events from disk and manage events
class TrackingEventQueue(
    private val fileUrl: URL
) : KoinComponent {

    private val eventRepository: EventRepository by inject()

    var events = mutableListOf<EventModel>()
        private set
    var isReady = false
        private set
    var isRunning = false
        private set

    companion object {
        const val TAG = "TrackingEventQueue"
    }

    /// Fetch unsent events from fileUrl
    init {
        try {
            val data = File(fileUrl.toURI()).readText()
            events = Json.decodeFromString<MutableList<EventModel>>(data)
        } catch (e: Exception) {
            events = mutableListOf()
        }
    }

    /// Set isReady flag
    /// If isReady sets  to True, Events will be saved to disk, The queue is ready to send data to backend
    /// If isReady sets to False, Events is not saved to the disk.
    fun setReady() {
        val wasReady = isReady
        isReady = true

        if (!wasReady) {
            save()
        }
    }

    /// Event still pushed to the queue if queue is not ready.
    fun push(event: EventModel) {
        events.add(event)
        save()
    }

    private fun willPop(): EventModel? {
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
            File(fileUrl.toURI()).writeText(data)
        } catch (e: Exception) {
            error("??? $e")
        }
    }

    /// Sending Event progress, Only one progress need to be ran at the time.
    suspend fun sendEventsIfNeeded() {
        if (!isReady || isRunning) return

        isRunning = true

        while (true) {
            val event = willPop() ?: break

            try {
                val request = EventTrackRequest.from(event)
                val response = eventRepository.rawTrack(request)
                response.status
            } catch (e: UnknownHostException) {
                Logger.d(TAG, "Tracking: failed ⛔️ + stopped ⛔️: $e")
                break
            } catch (e: Exception) {
//                val status = (e as? ApiException)?.httpStatus ?: 0
//                if (status >= 500) {
//                    Logger.d(TAG, "Tracking: failed ⛔️ + stopped ⛔️: $e")
//                    break
//                }

                Logger.d(TAG, "Tracking: failed ⛔️ + retry 🔁: $e")
            }

            pop()
        }

        isRunning = false
    }
}