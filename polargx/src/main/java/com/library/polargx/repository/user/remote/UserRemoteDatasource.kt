package com.library.polargx.repository.user.remote

import com.library.polargx.repository.user.remote.api.UpdateUserRequest
import io.ktor.client.statement.HttpResponse

interface UserRemoteDatasource {
    suspend fun updateUser(request: UpdateUserRequest?): HttpResponse
}