package com.library.link_attribution.repository.link.remote

import com.library.link_attribution.repository.link.remote.api.matching.GetLinkByMatchingRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.path

class LinkRemoteDatasourceImpl(
    private val client: HttpClient
) : LinkRemoteDatasource {

    companion object {
        const val TAG = ">>>UserRemoteDatasourceImpl"
    }

    override suspend fun getLinkByPath(
        appUnid: String?,
        apiKey: String?,
        path: String?
    ): HttpResponse {
        if (appUnid == null) throw Throwable(message = "appUnid is null!")
        if (apiKey == null) throw Throwable(message = "apiKey is null!")
        return client.get {
            url {
                path("api/v1/m/links/get-by-path")
                headers.append("app-unid", appUnid)
                headers.append("api-key", apiKey)
                parameters.append("path", path ?: "")
            }
        }
    }

    override suspend fun getLinkByMatching(
        appUnid: String?,
        apiKey: String?,
        request: GetLinkByMatchingRequest
    ): HttpResponse {
        if (appUnid == null) throw Throwable(message = "appUnid is null!")
        if (apiKey == null) throw Throwable(message = "apiKey is null!")
        return client.post {
            url.path("linkMatching")
            headers.append("app-unid", appUnid)
            headers.append("api-key", apiKey)
            setBody(request)
        }
    }

    override suspend fun getPublicLink(
        domain: String?,
        slug: String?
    ): HttpResponse {
        return client.get {
            url.path("sdk/v1/links/public")
            parameter("domain", domain)
            parameter("slug", slug)
        }
    }
}