package com.library.polar_gx.repository.event.local.model

import com.library.polar_gx.repository.event.model.EventModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EventEntity(
    @SerialName("organizationUnid")
    val organizationUnid: String? = null,
    @SerialName("eventName")
    val eventName: String? = null,
    @SerialName("data")
    val data: Map<String, String?>? = null
) {

    fun toExternal(): EventModel {
        return EventModel(
            organizationUnid = organizationUnid,
            eventName = eventName,
            data = data
        )
    }

    companion object {
        fun EventModel?.toEntity(): EventEntity? {
            if (this == null) return null
            return EventEntity(
                organizationUnid = organizationUnid,
                eventName = eventName,
                data = data
            )
        }
    }
}