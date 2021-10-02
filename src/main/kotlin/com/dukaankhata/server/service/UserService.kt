package com.dukaankhata.server.service

import com.dukaankhata.server.dto.*

abstract class UserService {
    abstract fun saveUser(): SavedUserResponse?
    abstract fun getUser(): SavedUserResponse?
    abstract fun getUserRoles(absoluteMobile: String): UserRoleResponse?
    abstract fun verifyPhone(absoluteMobile: String): PhoneVerificationResponse?
    abstract fun saveAddress(saveUserAddressRequest: SaveUserAddressRequest): SavedUserAddressResponse?
    abstract fun getAddresses(): UserAddressesResponse
    abstract fun registerNotificationSettings(notificationSettingsRequest: RegisterUserNotificationSettingsRequest): SavedUserResponse?
}
