package com.data.users.remote

import io.ktor.client.statement.HttpResponse


interface UserRemoteDatasource {

    suspend fun getProfile(): HttpResponse
}