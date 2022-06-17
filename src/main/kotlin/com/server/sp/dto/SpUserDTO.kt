package com.server.sp.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.server.common.dto.PaginationRequest
import com.server.common.dto.PaginationResponse
import com.server.common.enums.NotificationTokenProvider
import com.server.common.model.MediaDetailsV2

@JsonIgnoreProperties(ignoreUnknown = true)
data class SpUserPublicMiniDataResponse(
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
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SavedSpUserResponse(
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
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class RegisterUserNotificationSettingsRequest(
    val token: String = "",
    val tokenProvider: NotificationTokenProvider,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class UpdateSpUserHandleRequest (
    // Take it from request for any update related action
//    val userId: String,
    val newHandle: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class UpdateSpUserEmailRequest (
    // Take it from request for any update related action
//    val userId: String,
    val newEmail: String,
)

data class UpdateSpNotificationTokenRequest (
    val token: String,
    val tokenProvider: NotificationTokenProvider,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class UpdateSpUserDPRequest (
    // Take it from request for any update related action
//    val userId: String,
    val dp: MediaDetailsV2,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class UpdateSpUserCoverImageRequest (
    val coverImage: MediaDetailsV2,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class UpdateSpUserNameRequest (
    // Take it from request for any update related action
//    val userId: String,
    val newName: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class UpdateSpUserNameAndHandleRequest (
    // Take it from request for any update related action
//    val userId: String,
    val newName: String,
    val newHandle: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class UpdateSpUserDuringSignupRequest (
    // Take it from request for any update related action
//    val userId: String,
    val newName: String,
    val newHandle: String,
    val dp: MediaDetailsV2?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class UpdateSpUserContactVisibilityRequest (
    val contactVisible: Boolean,
)

data class SpUserHandleAvailabilityResponse(
    val available: Boolean,
)

data class GetAllSpUsersRequest (
    override val limit: Int = 10,
    override val pagingState: String? = null,
): PaginationRequest(limit, pagingState)

data class AllSpUsersResponse(
    val users: List<SavedSpUserResponse>,
    override val count: Int? = null,
    override val pagingState: String? = null,
    override val hasNext: Boolean? = null,
): PaginationResponse(count, pagingState, hasNext)
