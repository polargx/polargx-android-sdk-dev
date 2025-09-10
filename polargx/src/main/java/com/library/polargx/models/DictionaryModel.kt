import kotlinx.serialization.*
import kotlinx.serialization.json.*
import kotlinx.serialization.encoding.*
import kotlinx.serialization.descriptors.*

/**
 * Lớp dữ liệu tương đương với DictionaryModel trong Swift,
 * chứa một Map<String, Any?> để lưu trữ dữ liệu động.
 * Sử dụng một Serializer tùy chỉnh (DictionaryModelSerializer)
 * để xử lý việc mã hóa và giải mã JSON.
 */
@Serializable(with = DictionaryModelSerializer::class)
data class DictionaryModel(val content: Map<String, Any?>?) // Sử dụng Any? để có thể chứa null

/**
 * Serializer tùy chỉnh cho DictionaryModel.
 * Nó xử lý việc chuyển đổi giữa DictionaryModel và cấu trúc JsonObject.
 */
object DictionaryModelSerializer : KSerializer<DictionaryModel> {

    // Descriptor mô tả cấu trúc dữ liệu khi serialize, tương tự như một Map hoặc JsonObject.
    @OptIn(InternalSerializationApi::class)
    override val descriptor: SerialDescriptor =
        buildSerialDescriptor("DictionaryModel", StructureKind.MAP)

    override fun serialize(encoder: Encoder, value: DictionaryModel) {
        // Chỉ hoạt động với định dạng JSON
        val jsonEncoder = encoder as? JsonEncoder
            ?: throw SerializationException("This serializer can only be used with JSON format")

        // Xây dựng một JsonObject từ nội dung của DictionaryModel
        val jsonObject = buildJsonObject {
            value.content?.forEach { (key, mapValue) ->
                // Chuyển đổi từng giá trị trong Map thành JsonElement tương ứng
                put(key, anyToJsonElement(mapValue))
            }
        }
        // Mã hóa JsonObject đã tạo
        jsonEncoder.encodeJsonElement(jsonObject)
    }

    override fun deserialize(decoder: Decoder): DictionaryModel {
        // Chỉ hoạt động với định dạng JSON
        val jsonDecoder = decoder as? JsonDecoder
            ?: throw SerializationException("This serializer can only be used with JSON format")

        // Giải mã đầu vào thành một JsonObject
        val jsonObject = jsonDecoder.decodeJsonElement().jsonObject

        // Tạo một Map tạm thời để lưu kết quả giải mã
        val resultMap = mutableMapOf<String, Any?>()
        jsonObject.forEach { (key, jsonElement) ->
            // Chuyển đổi từng JsonElement trong JsonObject thành kiểu Kotlin tương ứng
            resultMap[key] = jsonElementToAny(jsonElement)
        }
        // Trả về một DictionaryModel mới với Map đã giải mã
        return DictionaryModel(resultMap)
    }

    /**
     * Hàm trợ giúp để chuyển đổi một giá trị Kotlin bất kỳ (Any?) thành JsonElement.
     */
    private fun anyToJsonElement(value: Any?): JsonElement = when (value) {
        null -> JsonNull // Kotlin null -> JsonNull
        is String -> JsonPrimitive(value) // String -> JsonPrimitive(String)
        is Boolean -> JsonPrimitive(value) // Boolean -> JsonPrimitive(Boolean)
        is Number -> JsonPrimitive(value) // Int, Long, Double, Float,... -> JsonPrimitive(Number)
        is Map<*, *> -> buildJsonObject { // Map -> JsonObject
            value.forEach { (mapKey, mapValue) ->
                // Key của Map phải là String để tương thích với JSON
                if (mapKey is String) {
                    put(mapKey, anyToJsonElement(mapValue)) // Đệ quy cho giá trị
                } else {
                    throw SerializationException("Map keys must be Strings for JSON serialization. Found key: $mapKey of type ${mapKey?.let { it::class }}")
                }
            }
        }
        is List<*> -> buildJsonArray { // List -> JsonArray
            value.forEach { listItem ->
                add(anyToJsonElement(listItem)) // Đệ quy cho từng phần tử
            }
        }
        // Có thể thêm các trường hợp khác nếu cần xử lý các kiểu tùy chỉnh
        else -> throw SerializationException("Cannot serialize type ${value::class}: $value")
    }

    /**
     * Hàm trợ giúp để chuyển đổi một JsonElement thành giá trị Kotlin Any?.
     */
    private fun jsonElementToAny(element: JsonElement): Any? = when (element) {
        is JsonNull -> null // JsonNull -> Kotlin null
        is JsonPrimitive -> when { // JsonPrimitive -> String, Boolean, Number
            element.isString -> element.content
            element.booleanOrNull != null -> element.boolean
            // Ưu tiên Long, sau đó Double để bao phủ nhiều loại số JSON
            element.longOrNull != null -> element.long
            element.doubleOrNull != null -> element.double
            else -> element.content // Trường hợp dự phòng nếu không parse được số
        }
        is JsonObject -> element.map { (key, value) ->
            // JsonObject -> Map<String, Any?>
            key to jsonElementToAny(value) // Đệ quy cho giá trị
        }.toMap()
        is JsonArray -> element.map { jsonElementToAny(it) } // JsonArray -> List<Any?> (đệ quy)
    }
}
