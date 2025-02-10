package com.library.link_attribution.repository.link.remote

import com.library.link_attribution.repository.link.remote.api.matching.GetLinkByMatchingRequest
import io.ktor.client.statement.HttpResponse


interface LinkRemoteDatasource {

    suspend fun getLinkByPath(
        appUnid: String?,
        apiKey: String?,
        path: String?,
    ): HttpResponse

    suspend fun getLinkByMatching(
        appUnid: String?,
        apiKey: String?,
        request: GetLinkByMatchingRequest
    ): HttpResponse
}