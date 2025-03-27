package com.library.polargx.repository.user

import com.library.polargx.model.empty.EmptyModel
import com.library.polargx.repository.user.remote.api.UpdateUserRequest

interface UserRepository {
    suspend fun updateUser(request: UpdateUserRequest?): EmptyModel?
}
