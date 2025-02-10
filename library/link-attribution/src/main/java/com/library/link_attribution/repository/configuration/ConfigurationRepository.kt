package com.library.link_attribution.repository.configuration

import com.library.link_attribution.repository.configuration.model.ConfigurationModel
import com.library.link_attribution.repository.configuration.model.InitSessionModel
import com.library.link_attribution.repository.configuration.remote.api.init.InitSessionRequest
import kotlinx.coroutines.flow.Flow

interface ConfigurationRepository {
    fun onAppDied()
    fun onLoggedOut()
    fun onTokenExpired()

    fun initSession(
        appUnid: String?,
        apiKey: String?,
        request: InitSessionRequest
    ): Flow<InitSessionModel?>

    fun getCacheConfiguration(): ConfigurationModel?
    fun setConfiguration(link: ConfigurationModel?)
    fun fetchAndCacheConfiguration(
        appUnid: String?,
        apiKey: String?,
    ): Flow<ConfigurationModel?>


}
