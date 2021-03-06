package com.server.common.service

import com.server.common.dto.*
import com.server.dk.dto.SaveUserAddressRequest
import com.server.dk.dto.SavedUserAddressResponse
import com.server.dk.dto.UserAddressesResponse

abstract class UserService {
    abstract fun saveUser(): SavedUserResponse?
    abstract fun getUser(): SavedUserResponse?
    abstract fun getUserRoles(absoluteMobile: String): UserRoleResponse?
    abstract fun verifyPhone(absoluteMobile: String): PhoneVerificationResponse?
    abstract fun saveAddress(saveUserAddressRequest: SaveUserAddressRequest): SavedUserAddressResponse?
    abstract fun getAddresses(): UserAddressesResponse
    abstract fun registerNotificationSettings(notificationSettingsRequest: RegisterUserNotificationSettingsRequest): SavedUserResponse?
    abstract fun updateDefaultAddress(request: UpdateDefaultAddressRequest): UserAddressesResponse?
}
