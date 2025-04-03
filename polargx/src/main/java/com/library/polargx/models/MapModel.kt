package com.library.polargx.models

import kotlinx.serialization.Contextual
import kotlinx.serialization.KSerializer
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.mapSerialDescriptor
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.double
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.int
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlinx.serialization.modules.SerializersModule

@Serializable
data class MapModel(val content: Map<String, @Contextual Any>?) {

    // Custom serializer for DynamicMap
    class DynamicMapSerializer : KSerializer<MapModel> {
        override val descriptor = buildClassSerialDescriptor("DynamicMap") {
            mapSerialDescriptor(
                String.serializer().descriptor,
                PolymorphicSerializer(Any::class).descriptor
            )
        }

        override fun serialize(encoder: Encoder, value: MapModel) {
            val jsonEncoder =
                encoder as? JsonEncoder ?: throw SerializationException("Expected JsonEncoder")
            jsonEncoder.encodeJsonElement(serializeContent(value.content ?: return))
        }

        override fun deserialize(decoder: Decoder): MapModel {
            val jsonDecoder =
                decoder as? JsonDecoder ?: throw SerializationException("Expected JsonDecoder")
            val jsonElement = jsonDecoder.decodeJsonElement()
            return MapModel(deserializeContent(jsonElement.jsonObject))
        }

        private fun serializeContent(content: Map<String, Any>): JsonObject {
            return buildJsonObject {
                content.forEach { (key, value) ->
                    when (value) {
                        is Boolean -> put(key, value)
                        is Int -> put(key, value)
                        is Double -> put(key, value)
                        is String -> put(key, value)
                        is Map<*, *> -> put(key, serializeContent(value as Map<String, Any>))
                        is List<*> -> put(
                            key,
                            JsonArray(value.map { JsonPrimitive(it.toString()) })
                        )

                        else -> put(key, JsonPrimitive(value.toString()))
                    }
                }
            }
        }

        private fun deserializeContent(jsonObject: JsonObject): Map<String, Any> {
            return jsonObject.mapValues { (_, value) ->
                when (value) {
                    is JsonPrimitive -> when {
                        value.isString -> value.content
                        value.booleanOrNull != null -> value.boolean
                        value.intOrNull != null -> value.int
                        value.doubleOrNull != null -> value.double
                        else -> value.content
                    }

                    is JsonObject -> deserializeContent(value)
                    is JsonArray -> value.map { it.jsonPrimitive.content }
                    else -> value.toString()
                }
            }
        }
    }

    companion object {
        private val json = Json {
            serializersModule =
                SerializersModule { contextual(MapModel::class, DynamicMapSerializer()) }
        }

        fun fromJson(jsonString: String): MapModel = json.decodeFromString(jsonString)
        fun toJson(model: MapModel): String = json.encodeToString(model)
    }
}
