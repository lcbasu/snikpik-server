package com.dukaankhata.server.service

import com.dukaankhata.server.dto.SavedUserResponse
import com.dukaankhata.server.dto.UserRoleResponse
import com.dukaankhata.server.dto.VerifyPhoneResponse

abstract class UserService {
    abstract fun saveUser(): SavedUserResponse?
    abstract fun getUser(): SavedUserResponse?
    abstract fun getUserRoles(phoneNumber: String): UserRoleResponse?
    abstract fun verifyPhone(phoneNumber: String): VerifyPhoneResponse?
}
