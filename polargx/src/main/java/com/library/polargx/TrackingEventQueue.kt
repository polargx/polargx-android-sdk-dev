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
import java.net.ConnectException
import java.net.UnknownHostException

/**
 * Purpose: fetch events from disk and manage events.
 */
class TrackingEventQueue(val file: File) : KoinComponent {

    private val eventRepository by inject<EventRepository>()

    var events = mutableListOf<EventModel>()
        private set
    var isReady = false
        private set
    var isRunning = false
        private set

    companion object {
        const val TAG = "TrackingEventQueue"
    }

    /**
     * Fetch unsent events from file.
     */
    init {
        try {
            val data = file.readText()
            events = Json.decodeFromString<MutableList<EventModel>>(data)
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
                val request = EventTrackRequest.from(event)
                val response = eventRepository.rawTrack(request)

                if (response.status.value >= 500) {
                    Logger.d(TAG, "Tracking: failed ⛔ + stopped ⛔")
                    break
                }
                //TODO: how about http status code is 4xx or 2xx? if status is 200 is success, otherwise throw error
            } catch (e: ConnectException) {
                Logger.d(TAG, "Tracking: failed ⛔ + stopped ⛔: $e")
                break
            } catch (e: UnknownHostException) {
                Logger.d(TAG, "Tracking: failed ⛔ + stopped ⛔: $e")
                break
            } catch (e: Exception) {
                Logger.d(TAG, "Tracking: failed ⛔ + retry 🔁: $e")
            }

            pop()
        }

        isRunning = false
    }
}