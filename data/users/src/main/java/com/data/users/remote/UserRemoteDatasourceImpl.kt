package com.data.users.remote

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.path

class UserRemoteDatasourceImpl(
    private val client: HttpClient
) : UserRemoteDatasource {

    companion object {
        const val TAG = ">>>UserRemoteDatasourceImpl"
    }

    override suspend fun getProfile(): HttpResponse {
        return client.get {
            url.path("users/profile")
        }
    }

}