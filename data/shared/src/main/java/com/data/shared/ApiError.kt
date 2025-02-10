package com.data.shared

import android.os.Parcelable
import com.app.shared.logger.DebugLogger
import com.data.shared.extension.json.getEvenNonExisted
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
) : Throwable(), Parcelable {

    constructor(data: String?) : this() {
        if (data == null) return
        try {
            val jsonObj = JSONObject(data)
            code = jsonObj.getEvenNonExisted("code") as? Int?
            message = jsonObj.getEvenNonExisted("message") as? String?
            apiVersion = jsonObj.getEvenNonExisted("api_version") as? Double?
        } catch (ex: Throwable) {
            DebugLogger.d("ApiError", "ex=$ex")
        }
    }
}