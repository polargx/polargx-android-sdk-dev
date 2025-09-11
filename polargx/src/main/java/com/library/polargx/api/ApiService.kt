package com.library.polargx.api

import android.content.Context
import com.library.polargx.api.device_tokens.deregister.DeregisterDeviceTokenRequest
import com.library.polargx.api.device_tokens.register.RegisterDeviceTokenRequest
import com.library.polargx.api.fcm_tokens.deregister.DeregisterFCMRequest
import com.library.polargx.api.update_user.UpdateUserRequest
import com.library.polargx.api.track_event.TrackEventRequest
import com.library.polargx.models.LinkClickModel
import com.library.polargx.models.LinkDataModel
import com.library.polargx.api.update_link.UpdateLinkClickRequest
import com.library.polargx.api.track_link.TrackLinkClickRequest
import com.library.polargx.api.fcm_tokens.register.RegisterFCMRequest
import com.library.polargx.models.ClientInfoModel

interface ApiService {

    // -------------------- Tracking --------------------

    suspend fun updateUser(request: UpdateUserRequest?)

    suspend fun trackEvent(request: TrackEventRequest?)

    // -------------------- Push --------------------

    suspend fun registerFCM(request: RegisterFCMRequest?)

    suspend fun deregisterFCM(request: DeregisterFCMRequest?)

    suspend fun registerDeviceToken(request: RegisterDeviceTokenRequest?)

    suspend fun deregisterDeviceToken(request: DeregisterDeviceTokenRequest?)

    // -------------------- Links --------------------

    suspend fun getLinkData(domain: String, slug: String): LinkDataModel?

    suspend fun trackLinkClick(request: TrackLinkClickRequest?): LinkClickModel?

    suspend fun updateLinkClick(clickUnid: String?, request: UpdateLinkClickRequest?)

    suspend fun matchLinkClick(fingerprint: String?): LinkClickModel?

    // -------------------- Other --------------------

    suspend fun getClientInfo(): ClientInfoModel?

    suspend fun getClientIP(): String?

    suspend fun isFirstTimeLaunch(context: Context?, nowInMillis: Long): Boolean
}