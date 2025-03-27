package com.library.polargx.repository.event

import android.content.Context
import com.library.polargx.model.ApiError
import com.library.polargx.model.empty.EmptyModel
import com.library.polargx.repository.event.local.EventLocalDatasource
import com.library.polargx.repository.event.local.model.EventEntity.Companion.toEntity
import com.library.polargx.repository.event.model.EventModel
import com.library.polargx.repository.event.remote.EventRemoteDatasource
import com.library.polargx.repository.event.remote.api.EventTrackRequest
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess

class EventRepositoryImpl(
    private val localDatasource: EventLocalDatasource,
    private val remoteDatasource: EventRemoteDatasource
) : EventRepository {

    companion object {
        const val TAG = ">>>EventRepositoryImpl"
    }

    private var mEventsList: List<EventModel>? = null

    override suspend fun onAppDied() {
        mEventsList = null
    }

    private suspend fun onUnauthenticated() {
        mEventsList = null
    }

    override suspend fun onLoggedOut() {
        onUnauthenticated()
    }

    override suspend fun onTokenExpired() {
        onUnauthenticated()
    }

    override suspend fun setCacheEventList(events: List<EventModel>?) {
        mEventsList = events
        val entities = events?.mapNotNull { it.toEntity() }
        localDatasource.setCacheEventList(entities)
    }

    override suspend fun getCacheEventList(): List<EventModel>? {
        if (mEventsList != null) return mEventsList
        val events = localDatasource.getAllCacheEventList()?.map { it.toExternal() }
        mEventsList = events
        return events
    }

    override suspend fun rawTrack(request: EventTrackRequest?): HttpResponse {
        return remoteDatasource.trackEvent(request = request)
    }

    override suspend fun trackEvent(request: EventTrackRequest?): EmptyModel? {
        val response = remoteDatasource.trackEvent(request)
        if (response.status.isSuccess()) {
            return response.body<EmptyModel?>()
        }
        throw ApiError(response.bodyAsText())
    }

    override suspend fun reset() {
        mEventsList = null
    }

    override suspend fun isFirstTimeLaunch(
        context: Context?,
        nowInMillis: Long
    ): Boolean {
        return localDatasource.isFirstTimeLaunch(
            context,
            nowInMillis
        )
    }
}
