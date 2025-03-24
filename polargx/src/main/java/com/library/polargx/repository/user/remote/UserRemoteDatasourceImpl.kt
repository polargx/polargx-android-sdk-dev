package com.library.polargx.repository.user.remote

import com.library.polargx.repository.user.remote.api.UpdateUserRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.path

class UserRemoteDatasourceImpl(
    private val client: HttpClient
) : UserRemoteDatasource {

    companion object {
        const val TAG = ">>>UserRemoteDatasourceImpl"
    }

    override suspend fun updateUser(request: UpdateUserRequest?): HttpResponse {
        return client.post {
            url.path("sdk/v1/users/profileUpdate")
            setBody(request)
        }
    }
}