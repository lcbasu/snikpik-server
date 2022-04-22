package com.server.common.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.server.common.enums.NotificationTokenProvider
import com.server.common.enums.ProfileType
import com.server.common.enums.UserLocationUpdateType
import com.server.common.model.MediaDetailsV2

/**
 *
 * Do not import any class or method that is not part of Common User Data
 *
 * */

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
data class RegisterUserNotificationSettingsRequest(
    val token: String = "",
    val tokenProvider: NotificationTokenProvider,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class UpdateDefaultAddressRequest(
    val defaultAddressId: String,
)


@JsonIgnoreProperties(ignoreUnknown = true)
data class UnblockUserRequest (
    val reportedByUserId: String, // Logged in user Id
    val toUnblockUserId: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class UnblockUserResponse (
    val reportedByUserId: String, // Logged in user Id
    val toUnblockUserId: String,
    val unblocked: Boolean,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class UpdateUserV2HandleRequest (
    // Take it from request for any update related action
//    val userId: String,
    val newHandle: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class UpdateUserV2EmailRequest (
    // Take it from request for any update related action
//    val userId: String,
    val newEmail: String,
)

data class UpdateNotificationTokenRequest (
    val token: String,
    val tokenProvider: NotificationTokenProvider,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class UpdateUserV2DPRequest (
    // Take it from request for any update related action
//    val userId: String,
    val dp: MediaDetailsV2,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class UpdateUserV2CoverImageRequest (
    val coverImage: MediaDetailsV2,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class UpdateUserV2BusinessSignupRequest (
    val email: String,
    val location: UpdateUserV2LocationRequest?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class UpdateUserV2ProfilesRequest (
    val profiles: Set<ProfileType>,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class UpdateUserV2NameRequest (
    // Take it from request for any update related action
//    val userId: String,
    val newName: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class UpdateUserV2NameAndHandleRequest (
    // Take it from request for any update related action
//    val userId: String,
    val newName: String,
    val newHandle: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class UpdateUserV2DuringSignupRequest (
    // Take it from request for any update related action
//    val userId: String,
    val newName: String,
    val newHandle: String,
    val dp: MediaDetailsV2?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class UpdateUserV2ContactVisibilityRequest (
    val contactVisible: Boolean,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class UpdateUserV2LocationRequest (
    // Take it from request for any update related action
//    val userId: String,
    val updateTypes: Set<UserLocationUpdateType>,
    val lat: Double,
    val lng: Double,
    val zipcode: String,
    val name: String?,
    val googlePlaceId: String?,
    val locality: String? = null,
    val subLocality: String? = null,
    val route: String? = null,
    val city: String? = null,
    val state: String? = null,
    val country: String? = null,
    val countryCode: String? = null,
    val completeAddress: String? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AWSLambdaAuthResponse(
    val userId: String,
    val anonymous: Boolean = false
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ProfilePageUserDetailsResponse(
    val userId: String,
    val fullName: String?,
    val uid: String?,
    val createdAt: Long?,
    val handle: String?,
    val email: String?, // Send to client only for Pros
    val absoluteMobile: String?, // Send to client only for Pros
    val dp: MediaDetailsV2?, // MediaDetailsV2
    val coverImage: MediaDetailsV2?, // MediaDetailsV2
    val verified: Boolean?,
    val contactVisible: Boolean?,
    val profileToShow: ProfileTypeResponse?,
    val allProfileTypes: AllProfileTypeResponse?,
    val userCurrentLocationName: String?,
    val userCurrentLocationZipcode: String?,
    val userPermanentLocationName: String?,
    val userPermanentLocationZipcode: String?,

    val currentLocationZipcode: String?,
    val currentGooglePlaceId: String?,
    val currentLocationId: String?,
    val currentLocationName: String?,
    val currentLocationLat: Double?,
    val currentLocationLng: Double?,
    val currentLocationLocality: String?,
    val currentLocationSubLocality: String?,
    val currentLocationRoute: String?,
    val currentLocationCity: String?,
    val currentLocationState: String?,
    val currentLocationCountry: String?,
    val currentLocationCountryCode: String?,
    val currentLocationCompleteAddress: String?,
    val permanentLocationZipcode: String?,
    val permanentGooglePlaceId: String?,
    val permanentLocationId: String?,
    val permanentLocationName: String?,
    val permanentLocationLat: Double?,
    val permanentLocationLng: Double?,
    val permanentLocationLocality: String?,
    val permanentLocationSubLocality: String?,
    val permanentLocationRoute: String?,
    val permanentLocationCity: String?,
    val permanentLocationState: String?,
    val permanentLocationCountry: String?,
    val permanentLocationCountryCode: String?,
    val permanentLocationCompleteAddress: String?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class UserV2PublicMiniDataResponse(
    val userId: String,
    val fullName: String?,
    val uid: String?,
    val createdAt: Long?,
    val handle: String?,
    val email: String?, // Send to client only for Pros
    val absoluteMobile: String?, // Send to client only for Pros
    val dp: MediaDetailsV2?, // MediaDetailsV2
    val coverImage: MediaDetailsV2?, // MediaDetailsV2
    val verified: Boolean?,
    val contactVisible: Boolean?,
    val profileToShow: ProfileTypeResponse?,
    val allProfileTypes: AllProfileTypeResponse?,

    val clZipcode: String?,
    val clId: String?,
    val clName: String?,
    val clCity: String?,
    val plZipcode: String?,
    val plId: String?,
    val plName: String?,
    val plCity: String?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SavedUserV2Response(
    val userId: String,
    val fullName: String?,
    val uid: String?,
    val anonymous: Boolean?,
    val absoluteMobile: String?,
    val email: String?,
    val countryCode: String?,
    val notificationToken: String?,
    val notificationTokenProvider: NotificationTokenProvider?,
    val createdAt: Long?,
    val handle: String?,
    val dp: MediaDetailsV2?, // MediaDetailsV2
    val coverImage: MediaDetailsV2?, // MediaDetailsV2
    val verified: Boolean?,
    val contactVisible: Boolean?,
    val profiles: AllProfileTypeResponse?,
    val preferredCategories: AllCategoryV2Response = AllCategoryV2Response(emptyList()),

    val currentLocationZipcode: String?,
    val currentGooglePlaceId: String?,
    val currentLocationId: String?,
    val currentLocationName: String?,
    val currentLocationLat: Double?,
    val currentLocationLng: Double?,
    val currentLocationLocality: String?,
    val currentLocationSubLocality: String?,
    val currentLocationRoute: String?,
    val currentLocationCity: String?,
    val currentLocationState: String?,
    val currentLocationCountry: String?,
    val currentLocationCountryCode: String?,
    val currentLocationCompleteAddress: String?,
    val permanentLocationZipcode: String?,
    val permanentGooglePlaceId: String?,
    val permanentLocationId: String?,
    val permanentLocationName: String?,
    val permanentLocationLat: Double?,
    val permanentLocationLng: Double?,
    val permanentLocationLocality: String?,
    val permanentLocationSubLocality: String?,
    val permanentLocationRoute: String?,
    val permanentLocationCity: String?,
    val permanentLocationState: String?,
    val permanentLocationCountry: String?,
    val permanentLocationCountryCode: String?,
    val permanentLocationCompleteAddress: String?,
)

data class UserHandleAvailabilityResponse(
    val available: Boolean,
)

