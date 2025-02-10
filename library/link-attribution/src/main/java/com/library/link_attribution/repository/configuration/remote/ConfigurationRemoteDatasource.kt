package com.library.link_attribution.repository.configuration.remote

import com.library.link_attribution.repository.configuration.remote.api.init.InitSessionRequest
import io.ktor.client.statement.HttpResponse

interface ConfigurationRemoteDatasource {

    suspend fun initSession(
        appUnid: String?,
        apiKey: String?,
        request: InitSessionRequest
    ): HttpResponse

    suspend fun getConfiguration(
        appUnid: String?,
        apiKey: String?,
    ): HttpResponse
}