package com.server.ud.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.server.common.dto.AllProfileTypeResponse
import com.server.common.dto.ProfileTypeResponse
import com.server.common.enums.NotificationTokenProvider
import com.server.common.enums.ProfileCategory
import com.server.common.enums.ProfileType
import com.server.common.model.MediaDetailsV2
import com.server.common.utils.DateUtils
import com.server.ud.dto.getCategories
import com.server.ud.entities.post.*
import com.server.ud.entities.user.*
import com.server.ud.enums.CategoryV2
import com.server.ud.enums.UserLocationUpdateType

data class LikedPostsByUserPostDetail(
    val likedByUserId: String,
    val likedAt: Long,
    override val postId: String,
    override val userId: String,
    override val createdAt: Long,
    override val media: MediaDetailsV2?,
    override val title: String?,
    override val description: String?
): PostMiniDetail

data class LikedPostsByUserResponse(
    val posts: List<LikedPostsByUserPostDetail>,
    override val count: Int? = null,
    override val pagingState: String? = null,
    override val hasNext: Boolean? = null,
): PaginationResponse(count, pagingState, hasNext)

data class LikedPostsByUserRequest (
    val userId: String,
    override val limit: Int = 10,
    override val pagingState: String? = null,
): PaginationRequest(limit, pagingState)

data class BookmarkedPostsByUserPostDetail(
    val bookmarkedByUserId: String,
    val bookmarkedAt: Long,
    override val postId: String,
    override val userId: String,
    override val createdAt: Long,
    override val media: MediaDetailsV2?,
    override val title: String?,
    override val description: String?
): PostMiniDetail

data class BookmarkedPostsByUserResponse(
    val posts: List<BookmarkedPostsByUserPostDetail>,
    override val count: Int? = null,
    override val pagingState: String? = null,
    override val hasNext: Boolean? = null,
): PaginationResponse(count, pagingState, hasNext)

data class BookmarkedPostsByUserResponseV2(
    val posts: List<SavedPostResponse>,
    override val count: Int? = null,
    override val pagingState: String? = null,
    override val hasNext: Boolean? = null,
): PaginationResponse(count, pagingState, hasNext)

data class BookmarkedPostsByUserRequest (
    val userId: String,
    override val limit: Int = 10,
    override val pagingState: String? = null,
): PaginationRequest(limit, pagingState)

data class BookmarkedPostsByUserRequestV2 (
    val userIdOrHandle: String,
    override val limit: Int = 10,
    override val pagingState: String? = null,
): PaginationRequest(limit, pagingState)

data class PostsByUserPostDetail(
    override val postId: String,
    override val userId: String,
    override val createdAt: Long,
    override val media: MediaDetailsV2?,
    override val title: String?,
    override val description: String?
): PostMiniDetail

data class PostsByUserResponse(
    val posts: List<PostsByUserPostDetail>,
    override val count: Int? = null,
    override val pagingState: String? = null,
    override val hasNext: Boolean? = null,
): PaginationResponse(count, pagingState, hasNext)

data class PostsByUserResponseV2(
    val posts: List<SavedPostResponse>,
    override val count: Int? = null,
    override val pagingState: String? = null,
    override val hasNext: Boolean? = null,
): PaginationResponse(count, pagingState, hasNext)

data class PostsByUserRequest (
    val userId: String,
    override val limit: Int = 10,
    override val pagingState: String? = null,
): PaginationRequest(limit, pagingState)

data class PostsByUserRequestV2 (
    val userIdOrHandle: String,
    override val limit: Int = 10,
    override val pagingState: String? = null,
): PaginationRequest(limit, pagingState)

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
data class UpdateUserV2PreferredCategoriesRequest (
    val categories: Set<CategoryV2>,
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
    val profileToShow: ProfileTypeResponse?,
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
    val profileToShow: ProfileTypeResponse?,

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

fun UserV2.toSavedUserV2Response(): SavedUserV2Response {
    this.apply {
        return SavedUserV2Response(
            userId = userId,
            fullName = fullName,
            uid = uid,
            anonymous = anonymous,
            absoluteMobile = absoluteMobile,
            email = email,
            countryCode = countryCode,
            notificationToken = notificationToken,
            notificationTokenProvider = notificationTokenProvider,
            createdAt = DateUtils.getEpoch(createdAt),
            handle = handle,
            dp = getMediaDetailsForDP(),
            coverImage = getMediaDetailsForCoverImage(),
            verified = verified,
            profiles = getProfiles(),
            preferredCategories = getPreferredCategories(),

            currentLocationId = currentLocationId,
            currentLocationLat = currentLocationLat,
            currentLocationLng = currentLocationLng,
            currentLocationZipcode = currentLocationZipcode,
            currentLocationName = currentLocationName,
            currentGooglePlaceId = currentGooglePlaceId,
            currentLocationLocality = currentLocationLocality,
            currentLocationSubLocality = currentLocationSubLocality,
            currentLocationRoute = currentLocationRoute,
            currentLocationCity = currentLocationCity,
            currentLocationState = currentLocationState,
            currentLocationCountry = currentLocationCountry,
            currentLocationCountryCode = currentLocationCountryCode,
            currentLocationCompleteAddress = currentLocationCompleteAddress,

            permanentLocationId = permanentLocationId,
            permanentLocationLat = permanentLocationLat,
            permanentLocationLng = permanentLocationLng,
            permanentLocationZipcode = permanentLocationZipcode,
            permanentLocationName = permanentLocationName,
            permanentGooglePlaceId = permanentGooglePlaceId,
            permanentLocationLocality = permanentLocationLocality,
            permanentLocationSubLocality = permanentLocationSubLocality,
            permanentLocationRoute = permanentLocationRoute,
            permanentLocationCity = permanentLocationCity,
            permanentLocationState = permanentLocationState,
            permanentLocationCountry = permanentLocationCountry,
            permanentLocationCountryCode = permanentLocationCountryCode,
            permanentLocationCompleteAddress = permanentLocationCompleteAddress,
        )
    }
}

fun UserV2.toProfilePageUserDetailsResponse(): ProfilePageUserDetailsResponse {
    this.apply {
        val profileToShow = getProfiles().profileTypes.firstOrNull()
        return ProfilePageUserDetailsResponse(
            userId = userId,
            fullName = fullName,
            uid = uid,
            createdAt = DateUtils.getEpoch(createdAt),
            handle = handle,
            email = if (profileToShow?.category !== ProfileCategory.OWNER) email else "",
            absoluteMobile = if (profileToShow?.category !== ProfileCategory.OWNER) absoluteMobile else "",
            dp = getMediaDetailsForDP(),
            coverImage = getMediaDetailsForCoverImage(),
            verified = verified,
            profileToShow = profileToShow,
            userCurrentLocationName = currentLocationName,
            userCurrentLocationZipcode = currentLocationZipcode,
            userPermanentLocationName = permanentLocationName,
            userPermanentLocationZipcode = permanentLocationZipcode,

            currentLocationId = currentLocationId,
            currentLocationLat = currentLocationLat,
            currentLocationLng = currentLocationLng,
            currentLocationZipcode = currentLocationZipcode,
            currentLocationName = currentLocationName,
            currentGooglePlaceId = currentGooglePlaceId,
            currentLocationLocality = currentLocationLocality,
            currentLocationSubLocality = currentLocationSubLocality,
            currentLocationRoute = currentLocationRoute,
            currentLocationCity = currentLocationCity,
            currentLocationState = currentLocationState,
            currentLocationCountry = currentLocationCountry,
            currentLocationCountryCode = currentLocationCountryCode,
            currentLocationCompleteAddress = currentLocationCompleteAddress,

            permanentLocationId = permanentLocationId,
            permanentLocationLat = permanentLocationLat,
            permanentLocationLng = permanentLocationLng,
            permanentLocationZipcode = permanentLocationZipcode,
            permanentLocationName = permanentLocationName,
            permanentGooglePlaceId = permanentGooglePlaceId,
            permanentLocationLocality = permanentLocationLocality,
            permanentLocationSubLocality = permanentLocationSubLocality,
            permanentLocationRoute = permanentLocationRoute,
            permanentLocationCity = permanentLocationCity,
            permanentLocationState = permanentLocationState,
            permanentLocationCountry = permanentLocationCountry,
            permanentLocationCountryCode = permanentLocationCountryCode,
            permanentLocationCompleteAddress = permanentLocationCompleteAddress,
        )
    }
}

fun UserV2.toUserV2PublicMiniDataResponse(): UserV2PublicMiniDataResponse {
    this.apply {
        val profileToShow = getProfiles().profileTypes.firstOrNull()
        return UserV2PublicMiniDataResponse(
            userId = userId,
            fullName = fullName,
            uid = uid,
            createdAt = DateUtils.getEpoch(createdAt),
            handle = handle,
            email = if (profileToShow?.category !== ProfileCategory.OWNER) email else "",
            absoluteMobile = if (profileToShow?.category !== ProfileCategory.OWNER) absoluteMobile else "",
            dp = getMediaDetailsForDP(),
            coverImage = getMediaDetailsForCoverImage(),
            verified = verified,
            profileToShow = profileToShow,

            clId = currentLocationId,
            clZipcode = currentLocationZipcode,
            clName = currentLocationName,
            clCity = currentLocationCity,

            plId = permanentLocationId,
            plZipcode = permanentLocationZipcode,
            plName = permanentLocationName,
            plCity = permanentLocationCity,
        )
    }
}

fun LikedPostsByUser.toLikedPostsByUserPostDetail(): LikedPostsByUserPostDetail {
    this.apply {
        return LikedPostsByUserPostDetail(
            postId = postId,
            userId = postedByUserId,
            media = getMediaDetails(),
            title = title,
            createdAt = DateUtils.getEpoch(postCreatedAt),
            likedAt = DateUtils.getEpoch(createdAt),
            likedByUserId = userId,
            description = description,
        )
    }
}

fun BookmarkedPostsByUser.toBookmarkedPostsByUserPostDetail(): BookmarkedPostsByUserPostDetail {
    this.apply {
        return BookmarkedPostsByUserPostDetail(
            postId = postId,
            userId = postedByUserId,
            media = getMediaDetails(),
            title = title,
            createdAt = DateUtils.getEpoch(postCreatedAt),
            bookmarkedAt = DateUtils.getEpoch(createdAt),
            bookmarkedByUserId = userId,
            description = description,
        )
    }
}


fun PostsByUser.toPostsByUserPostDetail(): PostsByUserPostDetail {
    this.apply {
        return PostsByUserPostDetail(
            postId = postId,
            userId = userId,
            media = getMediaDetails(),
            title = title,
            createdAt = DateUtils.getEpoch(createdAt),
            description = description,
        )
    }
}

fun PostsByUser.toSavedPostResponse(): SavedPostResponse {
    this.apply {
        return SavedPostResponse(
            postId = postId,
            postType = postType,
            userId = userId,
            locationId = locationId,
            zipcode = zipcode,
            googlePlaceId = null,
            locationName = locationName,
            locationLat = locationLat,
            locationLng = locationLng,
            locality = locality,
            subLocality = subLocality,
            route = route,
            city = city,
            state = state,
            country = country,
            countryCode = countryCode,
            completeAddress = completeAddress,
            createdAt = DateUtils.getEpoch(createdAt),
            title = title,
            description = description,
            tags = getHashTags(),
            categories = getCategories(),
            mediaDetails = getMediaDetails(),
            media = getMediaDetails(),
        )
    }
}

fun BookmarkedPostsByUser.toSavedPostResponse(): SavedPostResponse {
    this.apply {
        return SavedPostResponse(
            postId = postId,
            postType = postType,
            userId = userId,
            locationId = locationId,
            zipcode = zipcode,
            googlePlaceId = null,
            locationName = locationName,
            locationLat = locationLat,
            locationLng = locationLng,
            locality = locality,
            subLocality = subLocality,
            route = route,
            city = city,
            state = state,
            country = country,
            countryCode = countryCode,
            completeAddress = completeAddress,
            createdAt = DateUtils.getEpoch(createdAt),
            title = title,
            description = description,
            tags = getHashTags(),
            categories = getCategories(),
            mediaDetails = getMediaDetails(),
            media = getMediaDetails(),
        )
    }
}
