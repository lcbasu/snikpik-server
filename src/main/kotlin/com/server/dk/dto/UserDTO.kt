package com.server.dk.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.server.common.enums.NotificationTokenProvider
import com.server.dk.entities.Company
import com.server.dk.entities.Employee
import com.server.dk.entities.User
import com.server.dk.entities.UserRole

@JsonIgnoreProperties(ignoreUnknown = true)
data class SavedUserResponse(
    val serverId: String,
    val name: String,
    val uid: String,
    val anonymous: Boolean,
    val absoluteMobile: String,
    val countryCode: String,
    val defaultAddressId: String,
    val notificationToken: String,
    val notificationTokenProvider: NotificationTokenProvider,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class UserRoleResponse(
    val roles: Set<String>,
)

data class RequestContext (
    val user: User,
    val company: Company? = null,
    val employee: Employee? = null,
    val userRoles: List<UserRole> = emptyList(),
)

data class PhoneVerificationResponse (
    val valid: Boolean,
    val numberInInterNationalFormat: String? = null,
    val numberInNationalFormat: String? = null,
    val countryCode: String? = null,
    val callerName: String? = null,
    val callerType: String? = null,
    val carrierName: String? = null,
    val carrierType: String? = null,
    val mobileCountryCode: String? = null,
    val mobileNetworkCode: String? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SaveUserAddressRequest(
    val name: String = "",
    val address: SaveAddressRequest,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class RegisterUserNotificationSettingsRequest(
    val token: String = "",
    val tokenProvider: NotificationTokenProvider,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class UpdateDefaultAddressRequest(
    val defaultAddressId: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SavedUserAddressResponse(
    val user: SavedUserResponse,
    val address: SavedAddressResponse,
)

data class UserAddressesResponse(
    val user: SavedUserResponse,
    val addresses: List<SavedAddressResponse>
)

fun User.toSavedUserResponse(): SavedUserResponse {
    this.apply {
        return SavedUserResponse(
            serverId = id ?: "",
            name = fullName ?: "",
            uid = uid ?: "",
            anonymous = anonymous,
            absoluteMobile = absoluteMobile ?: "",
            countryCode = countryCode ?: "",
            defaultAddressId = defaultAddressId ?: "",
            notificationToken = notificationToken ?: "",
            notificationTokenProvider = notificationTokenProvider ?: NotificationTokenProvider.FIREBASE)
    }
}
