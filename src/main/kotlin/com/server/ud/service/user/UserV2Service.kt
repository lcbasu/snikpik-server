package com.server.ud.service.user

import com.server.common.dto.AllProfileTypeResponse
import com.server.common.enums.ProfileCategory
import com.server.ud.dto.*
import com.server.ud.entities.social.FollowersCountByUser
import com.server.ud.entities.social.FollowingsCountByUser

abstract class UserV2Service {
    abstract fun getUser(userId: String): SavedUserV2Response?
    abstract fun updateUserV2Handle(request: UpdateUserV2HandleRequest): SavedUserV2Response?
    abstract fun updateUserV2DP(request: UpdateUserV2DPRequest): SavedUserV2Response?
    abstract fun updateUserV2Profiles(request: UpdateUserV2ProfilesRequest): SavedUserV2Response?
    abstract fun updateUserV2Name(request: UpdateUserV2NameRequest): SavedUserV2Response?
    abstract fun updateUserV2Location(request: UpdateUserV2LocationRequest): SavedUserV2Response?
    abstract fun saveLoggedInUserV2(): SavedUserV2Response?
    abstract fun getLoggedInUserV2(): SavedUserV2Response?
    abstract fun getAWSLambdaAuthDetails(): AWSLambdaAuthResponse?
    abstract fun getUserDetailsForProfilePage(userId: String): ProfilePageUserDetailsResponse?
    abstract fun getLikedPostsByUser(request: LikedPostsByUserRequest): LikedPostsByUserResponse
    abstract fun getBookmarkedPostsByUser(request: BookmarkedPostsByUserRequest): BookmarkedPostsByUserResponse
    abstract fun getPostsByUser(request: PostsByUserRequest): PostsByUserResponse
    abstract fun getFollowersCountByUser(userId: String): FollowersCountByUser?
    abstract fun getFollowingsCountByUser(userId: String): FollowingsCountByUser?
    abstract fun saveLoggedInUserV2WithIPLocation(request: IPLocationData?): SavedUserV2Response?
    abstract fun isUserHandleAvailable(handle: String): UserHandleAvailabilityResponse
    abstract fun updateUserV2DuringSignup(request: UpdateUserV2DuringSignupRequest): SavedUserV2Response?
    abstract fun updateUserV2BusinessDuringSignup(request: UpdateUserV2BusinessSignupRequest): SavedUserV2Response?
    abstract fun updateUserV2PreferredCategories(request: UpdateUserV2PreferredCategoriesRequest): SavedUserV2Response?
    abstract fun getProfileTypesByProfileCategory(profileCategory: ProfileCategory): AllProfileTypeResponse?
    abstract fun getAllProfileTypes(): AllProfileTypeResponse?
}
