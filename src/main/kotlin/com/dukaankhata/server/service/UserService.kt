package com.dukaankhata.server.service

import com.dukaankhata.server.dto.*

abstract class UserService {
    abstract fun saveUser(): SavedUserResponse?
    abstract fun getUser(): SavedUserResponse?
    abstract fun getUserRoles(phoneNumber: String): UserRoleResponse?
    abstract fun verifyPhone(phoneNumber: String): VerifyPhoneResponse?
    abstract fun saveAddress(saveUserAddressRequest: SaveUserAddressRequest): SavedUserAddressResponse?
    abstract fun getAddresses(): UserAddressesResponse
}
