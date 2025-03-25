package com.library.polargx.repository.user

import com.library.polargx.repository.user.remote.api.UpdateUserRequest
import io.ktor.client.statement.HttpResponse

interface UserRepository {
    suspend fun updateUser(request: UpdateUserRequest?): HttpResponse
}
