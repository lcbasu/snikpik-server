package com.dukaankhata.server.service

import com.dukaankhata.server.dto.user.SaveUserRequest
import com.dukaankhata.server.dto.user.SavedUserResponse
import com.dukaankhata.server.model.Credentials

open class UserService {
    open fun saveUser(saveUserRequest: SaveUserRequest): SavedUserResponse? = null
    open fun getUser(): SavedUserResponse? = null
}
