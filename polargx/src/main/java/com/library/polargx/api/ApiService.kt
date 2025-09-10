package com.library.polargx.api

import android.content.Context
import com.library.polargx.api.deregister_fcm.DeregisterFCMRequest
import com.library.polargx.api.update_user.UpdateUserRequest
import com.library.polargx.api.track_event.TrackEventRequest
import com.library.polargx.models.LinkClickModel
import com.library.polargx.models.LinkDataModel
import com.library.polargx.api.update_link.UpdateLinkClickRequest
import com.library.polargx.api.track_link.TrackLinkClickRequest
import com.library.polargx.api.register_fcm.RegisterFCMRequest

interface ApiService {
    suspend fun updateUser(request: UpdateUserRequest?)

    suspend fun registerFCM(request: RegisterFCMRequest?)

    suspend fun deregisterFCM(request: DeregisterFCMRequest?)

    suspend fun trackEvent(request: TrackEventRequest?)

    suspend fun getLinkData(domain: String, slug: String): LinkDataModel?

    suspend fun trackLinkClick(request: TrackLinkClickRequest?): LinkClickModel?

    suspend fun updateLinkClick(clickUnid: String?, request: UpdateLinkClickRequest?)

    suspend fun matchLinkClick(fingerprint: String?)

    suspend fun isFirstTimeLaunch(context: Context?, nowInMillis: Long): Boolean
}