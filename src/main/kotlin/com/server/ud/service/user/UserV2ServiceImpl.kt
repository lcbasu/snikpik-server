package com.server.ud.service.user

import com.server.common.dto.AllProfileTypeResponse
import com.server.common.dto.UpdateUserV2PreferredCategoriesRequest
import com.server.common.enums.ProfileCategory
import com.server.common.provider.SecurityProvider
import com.server.common.dto.*
import com.server.dk.dto.AllUserReportResponse
import com.server.dk.dto.UserReportRequest
import com.server.dk.dto.UserReportResponse
import com.server.ud.dto.*
import com.server.ud.entities.post.toLikedPostsByUserPostDetail
import com.server.ud.entities.social.FollowersCountByUser
import com.server.ud.entities.social.FollowingsCountByUser
import com.server.ud.entities.user.toProfilePageUserDetailsResponse
import com.server.ud.entities.user.toSavedUserV2Response
import com.server.ud.entities.user.toUserV2PublicMiniDataResponse
import com.server.ud.provider.post.BookmarkedPostsByUserProvider
import com.server.ud.provider.post.LikedPostsByUserProvider
import com.server.ud.provider.post.PostsByUserProvider
import com.server.ud.provider.social.FollowersCountByUserProvider
import com.server.ud.provider.social.FollowingsCountByUserProvider
import com.server.ud.provider.user.UserV2Provider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserV2ServiceImpl : UserV2Service() {

    @Autowired
    private lateinit var userV2Provider: UserV2Provider

    @Autowired
    private lateinit var postsByUserProvider: PostsByUserProvider

    @Autowired
    private lateinit var likedPostsByUserProvider: LikedPostsByUserProvider

    @Autowired
    private lateinit var bookmarkedPostsByUserProvider: BookmarkedPostsByUserProvider

    @Autowired
    private lateinit var followersCountByUserProvider: FollowersCountByUserProvider

    @Autowired
    private lateinit var followingsCountByUserProvider: FollowingsCountByUserProvider

    @Autowired
    private lateinit var securityProvider: SecurityProvider

    override fun getUser(userId: String): SavedUserV2Response? {
        return userV2Provider.getUser(userId)?.toSavedUserV2Response()
    }

    override fun updateUserV2Handle(request: UpdateUserV2HandleRequest): SavedUserV2Response? {
        return userV2Provider.updateUserV2Handle(request)?.toSavedUserV2Response()
    }

    override fun updateUserV2DP(request: UpdateUserV2DPRequest): SavedUserV2Response? {
        return userV2Provider.updateUserV2DP(request)?.toSavedUserV2Response()
    }

    override fun updateUserV2Profiles(request: UpdateUserV2ProfilesRequest): SavedUserV2Response? {
        return userV2Provider.updateUserV2Profiles(request)?.toSavedUserV2Response()
    }

    override fun updateUserV2Name(request: UpdateUserV2NameRequest): SavedUserV2Response? {
        return userV2Provider.updateUserV2Name(request)?.toSavedUserV2Response()
    }

    override fun updateUserV2Location(request: UpdateUserV2LocationRequest): SavedUserV2Response? {
        val firebaseAuthUser = securityProvider.validateRequest()
        return userV2Provider.updateUserV2Location(request, firebaseAuthUser.getUserIdToUse())?.toSavedUserV2Response()
    }

    override fun saveLoggedInUserV2(): SavedUserV2Response? {
        return userV2Provider.saveUserV2WhoJustLoggedIn()?.toSavedUserV2Response()
    }

    override fun getLoggedInUserV2(): SavedUserV2Response? {
        return userV2Provider.getLoggedInUserV2()?.toSavedUserV2Response()
    }

    override fun getAWSLambdaAuthDetails(): AWSLambdaAuthResponse? {
        return userV2Provider.getAWSLambdaAuthDetails()
    }

    override fun getUserDetailsForProfilePage(userId: String): ProfilePageUserDetailsResponse? {
        return userV2Provider.getUser(userId)?.toProfilePageUserDetailsResponse()
    }

    override fun getActivityByUserData(userId: String): ActivityByUserData? {
        return userV2Provider.getUser(userId)?.toActivityByUserData()
    }

    override fun getLikedPostsByUser(request: LikedPostsByUserRequest): LikedPostsByUserResponse {
        val result = likedPostsByUserProvider.getLikedPostsByUser(request)
        return LikedPostsByUserResponse(
            posts = result.content?.filterNotNull()?.map { it.toLikedPostsByUserPostDetail() } ?: emptyList(),
            count = result.count,
            hasNext = result.hasNext,
            pagingState = result.pagingState
        )
    }

    override fun getBookmarkedPostsByUser(request: BookmarkedPostsByUserRequest): BookmarkedPostsByUserResponse {
        return bookmarkedPostsByUserProvider.getBookmarkedPostsByUserResponse(request)
    }

    override fun getPostsByUser(request: PostsByUserRequest): PostsByUserResponse {
        return postsByUserProvider.getPostsByUserResponse(request)
    }

    override fun getFollowersCountByUser(userId: String): FollowersCountByUser? {
        return followersCountByUserProvider.getFollowersCountByUser(userId)
    }

    override fun getFollowingsCountByUser(userId: String): FollowingsCountByUser? {
        return followingsCountByUserProvider.getFollowingsCountByUser(userId)
    }

    override fun saveLoggedInUserV2WithIPLocation(request: IPLocationData?): SavedUserV2Response? {
        return userV2Provider.saveLoggedInUserV2WithIPLocation(request)?.toSavedUserV2Response()
    }

    override fun isUserHandleAvailable(handle: String): UserHandleAvailabilityResponse {
        return UserHandleAvailabilityResponse(
            available = userV2Provider.isUserHandleAvailable(handle)
        )
    }

    override fun updateUserV2DuringSignup(request: UpdateUserV2DuringSignupRequest): SavedUserV2Response? {
        return userV2Provider.updateUserV2DuringSignup(request)?.toSavedUserV2Response()
    }

    override fun updateUserV2BusinessDuringSignup(request: UpdateUserV2BusinessSignupRequest): SavedUserV2Response? {
        return userV2Provider.updateUserV2BusinessDuringSignup(request)?.toSavedUserV2Response()
    }

    override fun updateUserV2PreferredCategories(request: UpdateUserV2PreferredCategoriesRequest): SavedUserV2Response? {
        return userV2Provider.updateUserV2PreferredCategories(request)?.toSavedUserV2Response()
    }

    override fun updateUserV2Email(request: UpdateUserV2EmailRequest): SavedUserV2Response? {
        return userV2Provider.updateUserV2Email(request)?.toSavedUserV2Response()
    }

    override fun updateContactVisibility(request: UpdateUserV2ContactVisibilityRequest): SavedUserV2Response? {
        return userV2Provider.updateContactVisibility(request)?.toSavedUserV2Response()
    }

    override fun removeUserV2DP(): SavedUserV2Response? {
        return userV2Provider.removeUserV2DP()?.toSavedUserV2Response()
    }

    override fun updateUserV2CoverImage(request: UpdateUserV2CoverImageRequest): SavedUserV2Response? {
        return userV2Provider.updateUserV2CoverImage(request)?.toSavedUserV2Response()
    }

    override fun updateNotificationToken(request: UpdateNotificationTokenRequest): SavedUserV2Response? {
        return userV2Provider.updateNotificationToken(request)?.toSavedUserV2Response()
    }

    override fun getUserPublicData(userId: String): UserV2PublicMiniDataResponse? {
        return userV2Provider.getUser(userId)?.toUserV2PublicMiniDataResponse()
    }

    override fun removeUserV2Handle(): SavedUserV2Response? {
        return userV2Provider.removeUserV2Handle()?.toSavedUserV2Response()
    }

    override fun report(request: UserReportRequest): UserReportResponse? {
        return userV2Provider.report(request)
    }

    override fun getAllReport(userId: String): AllUserReportResponse? {
        return userV2Provider.getAllReport(userId)
    }

    override fun unblockUser(request: UnblockUserRequest): UnblockUserResponse? {
        return userV2Provider.unblockUser(request)
    }

    override fun getUserPublicDetails(userIdOrHandle: String): UserV2PublicMiniDataResponse {
        val user = userV2Provider.getUserByIdOrHandle(userIdOrHandle) ?: error("User not found for userIdOrHandle: $userIdOrHandle")
        return user.toUserV2PublicMiniDataResponse()
    }

    override fun getProfileTypesByProfileCategory(profileCategory: ProfileCategory): AllProfileTypeResponse? {
        return userV2Provider.getProfileTypesByProfileCategory(profileCategory)
    }

    override fun getAllProfileTypes(): AllProfileTypeResponse? {
        return userV2Provider.getAllProfileTypes()
    }

}
