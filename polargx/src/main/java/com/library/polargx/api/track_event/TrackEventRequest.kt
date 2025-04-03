package com.library.polargx.api.track_event

import com.library.polargx.models.MapModel
import com.library.polargx.models.TrackEventModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TrackEventRequest(
    @SerialName("organizationUnid")
    val organizationUnid: String?,
    @SerialName("userID")
    val userID: String?,
    @SerialName("eventName")
    val eventName: String?,
    @SerialName("eventTime")
    val eventTime: String?,
    @SerialName("data")
    val data: MapModel?
) {

    companion object {
        fun from(event: TrackEventModel?): TrackEventRequest? {
            if (event == null) return null
            return TrackEventRequest(
                organizationUnid = event.organizationUnid,
                userID = event.userID,
                eventName = event.eventName,
                eventTime = event.eventTime,
                data = event.data
            )
        }
    }
}