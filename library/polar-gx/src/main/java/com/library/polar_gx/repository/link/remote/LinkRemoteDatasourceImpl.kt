package com.library.polar_gx.repository.link.remote

import com.library.polar_gx.repository.link.remote.api.click.LinkClickRequest
import com.library.polar_gx.repository.link.remote.api.track.LinkTrackRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.path

class LinkRemoteDatasourceImpl(
    private val client: HttpClient
) : LinkRemoteDatasource {

    companion object {
        const val TAG = ">>>LinkRemoteDatasourceImpl"
    }

    override suspend fun fetchLinkData(
        domain: String,
        slug: String
    ): HttpResponse {
        return client.get {
            url {
                path("sdk/v1/links/data")
                parameters.append("domain", domain)
                parameters.append("slug", slug)
            }
        }
    }

    override suspend fun fetchOrganization(domain: String): HttpResponse {
        return client.get {
            url {
                path("sdk/v1/links/domain/data")
                parameters.append("domain", domain)
            }
        }
    }

    override suspend fun fetchLinkMatches(fingerprint: String): HttpResponse {
        return client.get {
            url {
                path("sdk/v1/links/matches/fingerprint")
                parameters.append("fingerprint", fingerprint)
            }
        }
    }

    override suspend fun track(request: LinkTrackRequest?): HttpResponse {
        return client.post {
            url {
                path("sdk/v1/links/track")
                setBody(request)
            }
        }
    }

    override suspend fun linkClick(
        linkClickUnid: String?,
        request: LinkClickRequest?
    ): HttpResponse {
        return client.put {
            url {
                path("sdk/v1/links/clicks/$linkClickUnid")
                setBody(request)
            }
        }
    }
}