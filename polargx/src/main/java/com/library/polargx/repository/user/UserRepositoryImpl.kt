package com.library.polargx.repository.user

import com.library.polargx.model.ApiError
import com.library.polargx.repository.user.local.UserLocalDatasource
import com.library.polargx.repository.user.remote.UserRemoteDatasource
import com.library.polargx.repository.user.remote.api.UpdateUserRequest
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess

class UserRepositoryImpl(
    private val localDatasource: UserLocalDatasource,
    private val remoteDatasource: UserRemoteDatasource
) : UserRepository {

    companion object {
        const val TAG = ">>>UserRepositoryImpl"
    }

    override suspend fun updateUser(request: UpdateUserRequest?): HttpResponse {
        val response = remoteDatasource.updateUser(request)
        return if (response.status.isSuccess()) {
            response
        } else {
            throw ApiError(response.bodyAsText())
        }
    }
}
