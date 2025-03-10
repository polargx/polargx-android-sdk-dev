package com.library.polar_gx.repository.event

import android.content.Context
import com.library.polar_gx.repository.event.model.EventModel
import com.library.polar_gx.repository.event.remote.api.EventTrackRequest
import com.library.polar_gx.repository.event.remote.api.EventTrackResponse
import io.ktor.client.statement.HttpResponse

interface EventRepository {
    suspend fun onAppDied()
    suspend fun onLoggedOut()
    suspend fun onTokenExpired()

    suspend fun setCacheEventList(events: List<EventModel>?)
    suspend fun getCacheEventList(): List<EventModel>?
    suspend fun rawTrack(request: EventTrackRequest?): HttpResponse
    suspend fun track(request: EventTrackRequest?): EventTrackResponse
    suspend fun reset()

    suspend fun isFirstTimeLaunch(
        context: Context?,
        nowInMillis: Long
    ): Boolean
}
