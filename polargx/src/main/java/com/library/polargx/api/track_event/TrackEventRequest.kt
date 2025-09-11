package com.library.polargx.api.track_event

import DictionaryModel
import com.library.polargx.models.TrackEventModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TrackEventRequest(
    @SerialName("clobberMatchingAttributes")
    val clobberMatchingAttributes: Boolean?,
    @SerialName("organizationUnid")
    val organizationUnid: String?,
    @SerialName("userID")
    val userID: String?,
    @SerialName("eventName")
    val eventName: String?,
    @SerialName("eventTime")
    val eventTime: String?,
    @SerialName("eventUnid")
    val eventUnid: String?,
    @SerialName("data")
    val data: DictionaryModel?
) {

    companion object {
        fun from(event: TrackEventModel?): TrackEventRequest? {
            if (event == null) return null
            return TrackEventRequest(
                clobberMatchingAttributes = event.clobberMatchingAttributes,
                organizationUnid = event.organizationUnid,
                userID = event.userID,
                eventName = event.eventName,
                eventTime = event.eventTime,
                eventUnid = event.eventUnid,
                data = event.data
            )
        }
    }
}