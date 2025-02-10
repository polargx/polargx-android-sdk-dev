package com.library.link_attribution.repository.configuration

import com.data.shared.ApiError
import com.library.link_attribution.repository.configuration.local.ConfigurationLocalDatasource
import com.library.link_attribution.repository.configuration.local.model.ConfigurationEntity.Companion.toEntity
import com.library.link_attribution.repository.configuration.local.model.InitSessionEntity.Companion.toEntity
import com.library.link_attribution.repository.configuration.model.ConfigurationModel
import com.library.link_attribution.repository.configuration.model.InitSessionModel
import com.library.link_attribution.repository.configuration.remote.ConfigurationRemoteDatasource
import com.library.link_attribution.repository.configuration.remote.api.GetConfigurationResponse
import com.library.link_attribution.repository.configuration.remote.api.init.InitSessionRequest
import com.library.link_attribution.repository.configuration.remote.api.init.InitSessionResponse
import io.ktor.client.call.body
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ConfigurationRepositoryImpl(
    private val localDatasource: ConfigurationLocalDatasource,
    private val remoteDatasource: ConfigurationRemoteDatasource
) : ConfigurationRepository {

    companion object {
        const val TAG = ">>>ConfigurationRepositoryImpl"
    }

    private var mInitSession: InitSessionModel? = null
    private var mConfiguration: ConfigurationModel? = null

    override fun onAppDied() {
        mInitSession = null
        mConfiguration = null
    }

    private fun onUnauthenticated() {
        mInitSession = null
        mConfiguration = null
    }

    override fun onLoggedOut() {
        onUnauthenticated()
    }

    override fun onTokenExpired() {
        onUnauthenticated()
    }

    override fun initSession(
        appUnid: String?,
        apiKey: String?,
        request: InitSessionRequest
    ): Flow<InitSessionModel?> {
        return flow {
            val response = remoteDatasource.initSession(
                appUnid = appUnid,
                apiKey = apiKey,
                request = request
            )
            if (response.status.isSuccess()) {
                val body = response.body<InitSessionResponse>()
                mInitSession = body.data?.initSession?.toExternal()
                localDatasource.setInitSession(mInitSession?.toEntity())
                emit(mInitSession)
            } else {
                throw ApiError(response.bodyAsText())
            }
        }
    }

    override fun getCacheConfiguration(): ConfigurationModel? {
        if (mConfiguration == null) {
            mConfiguration = localDatasource.getConfiguration()?.toExternal()
        }
        return mConfiguration
    }

    override fun setConfiguration(link: ConfigurationModel?) {
        mConfiguration = link
        localDatasource.setConfiguration(link?.toEntity())
    }

    override fun fetchAndCacheConfiguration(
        appUnid: String?,
        apiKey: String?
    ): Flow<ConfigurationModel?> {
        return flow {
            val response = remoteDatasource.getConfiguration(
                appUnid = appUnid,
                apiKey = apiKey,
            )
            if (response.status.isSuccess()) {
                val body = response.body<GetConfigurationResponse>()
                mConfiguration = body.data?.configuration?.toExternal()
                localDatasource.setConfiguration(mConfiguration?.toEntity())
                emit(mConfiguration)
            } else {
                throw ApiError(response.bodyAsText())
            }
        }
    }

}
