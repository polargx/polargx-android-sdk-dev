package com.library.polargx.helpers

import android.os.Parcelable
import com.library.polargx.extension.getEvenNonExisted
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.json.JSONObject

@Parcelize
@Serializable
data class ApiError(
    @SerialName("code")
    var code: Int? = null,

    @SerialName("message")
    override var message: String? = null,

    @SerialName("api_version")
    var apiVersion: Double? = null,
) : Exception(), Parcelable {

    constructor(data: String?) : this() {
        if (data == null) return
        try {
            val jsonObj = JSONObject(data)
            code = jsonObj.getEvenNonExisted("code") as? Int?
            message = jsonObj.getEvenNonExisted("message") as? String?
            apiVersion = jsonObj.getEvenNonExisted("api_version") as? Double?
        } catch (e: Exception) {
            Logger.d("ApiError", "error: $e")
        }
    }
}