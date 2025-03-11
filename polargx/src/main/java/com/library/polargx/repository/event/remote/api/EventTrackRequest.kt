package com.library.polargx.repository.event.remote.api

import com.library.polargx.repository.event.model.EventModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EventTrackRequest(
    @SerialName("organizationUnid")
    val organizationUnid: String? = null,
    @SerialName("eventName")
    val eventName: String? = null,
    @SerialName("eventTime")
    val eventTime: String? = null,
    @SerialName("data")
    val data: Map<String, String?>? = null
) {

    companion object {
        fun from(event: EventModel?): EventTrackRequest? {
            if (event == null) return null
            return EventTrackRequest(
                organizationUnid = event.organizationUnid,
                eventName = event.eventName,
                eventTime = event.eventTime,
                data = event.data
            )
        }
    }
}