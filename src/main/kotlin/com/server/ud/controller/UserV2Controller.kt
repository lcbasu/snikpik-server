package com.server.ud.controller

import com.server.common.dto.AllProfileTypeResponse
import com.server.common.enums.ProfileCategory
import com.server.common.provider.SecurityProvider
import com.server.ud.dto.*
import com.server.ud.entities.social.FollowersCountByUser
import com.server.ud.entities.social.FollowingsCountByUser
import com.server.ud.service.user.UserV2Service
import io.micrometer.core.annotation.Timed
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@Timed
@RequestMapping("ud/userV2")
class UserV2Controller {

    @Autowired
    private lateinit var securityProvider: SecurityProvider

    @Autowired
    private lateinit var userV2Service: UserV2Service

    @RequestMapping(value = ["/save"], method = [RequestMethod.POST])
    fun saveLoggedInUserV2(): SavedUserV2Response? {
        securityProvider.validateRequest()
        return userV2Service.saveLoggedInUserV2()
    }

    @RequestMapping(value = ["/saveWithIPLocation"], method = [RequestMethod.POST])
    fun saveLoggedInUserV2WithIPLocation(@RequestBody request: IPLocationData?): SavedUserV2Response? {
        securityProvider.validateRequest()
        return userV2Service.saveLoggedInUserV2WithIPLocation(request)
    }

    @RequestMapping(value = ["/get"], method = [RequestMethod.GET])
    fun getLoggedInUserV2(): SavedUserV2Response? {
        securityProvider.validateRequest()
        return userV2Service.getLoggedInUserV2()
    }

    @RequestMapping(value = ["/getAWSLambdaAuthDetails"], method = [RequestMethod.GET])
    fun getAWSLambdaAuthDetails(): AWSLambdaAuthResponse? {
        securityProvider.validateRequest()
        return userV2Service.getAWSLambdaAuthDetails()
    }

    @RequestMapping(value = ["/getUser"], method = [RequestMethod.GET])
    fun getUser(@RequestParam userId: String): SavedUserV2Response? {
        securityProvider.validateRequest()
        return userV2Service.getUser(userId)
    }

    @RequestMapping(value = ["/getUserPublicData"], method = [RequestMethod.GET])
    fun getUserPublicData(@RequestParam userId: String): UserV2PublicMiniDataResponse? {
        securityProvider.validateRequest()
        return userV2Service.getUserPublicData(userId)
    }

    @RequestMapping(value = ["/getFollowersCountByUser"], method = [RequestMethod.GET])
    fun getFollowersCountByUser(@RequestParam userId: String): FollowersCountByUser? {
        return userV2Service.getFollowersCountByUser(userId)
    }

    @RequestMapping(value = ["/getFollowingsCountByUser"], method = [RequestMethod.GET])
    fun getFollowingsCountByUser(@RequestParam userId: String): FollowingsCountByUser? {
        return userV2Service.getFollowingsCountByUser(userId)
    }

    @RequestMapping(value = ["/getPostsByUser"], method = [RequestMethod.GET])
    fun getPostsByUser(@RequestParam userId: String,
                       @RequestParam limit: Int,
                       @RequestParam pagingState: String?): PostsByUserResponse {
        securityProvider.validateRequest()
        return userV2Service.getPostsByUser(
            PostsByUserRequest(
                userId,
                limit,
                pagingState
            )
        )
    }

    @RequestMapping(value = ["/getLikedPostsByUser"], method = [RequestMethod.GET])
    fun getLikedPostsByUser(@RequestParam userId: String,
                            @RequestParam limit: Int,
                            @RequestParam pagingState: String?): LikedPostsByUserResponse {
        securityProvider.validateRequest()
        return userV2Service.getLikedPostsByUser(
            LikedPostsByUserRequest(
                userId,
                limit,
                pagingState
            )
        )
    }

    @RequestMapping(value = ["/getBookmarkedPostsByUser"], method = [RequestMethod.GET])
    fun getBookmarkedPostsByUser(@RequestParam userId: String,
                                 @RequestParam limit: Int,
                                 @RequestParam pagingState: String?): BookmarkedPostsByUserResponse {
        securityProvider.validateRequest()
        return userV2Service.getBookmarkedPostsByUser(
            BookmarkedPostsByUserRequest(
                userId,
                limit,
                pagingState
            )
        )
    }

    @RequestMapping(value = ["/getUserDetailsForProfilePage"], method = [RequestMethod.GET])
    fun getUserDetailsForProfilePage(@RequestParam userId: String): ProfilePageUserDetailsResponse? {
        securityProvider.validateRequest()
        return userV2Service.getUserDetailsForProfilePage(userId)
    }

    @RequestMapping(value = ["/getActivityByUserData"], method = [RequestMethod.GET])
    fun getActivityByUserData(@RequestParam userId: String): ActivityByUserData? {
        securityProvider.validateRequest()
        return userV2Service.getActivityByUserData(userId)
    }

    @RequestMapping(value = ["/updateUserV2Handle"], method = [RequestMethod.POST])
    fun updateUserV2Handle(@RequestBody request: UpdateUserV2HandleRequest): SavedUserV2Response? {
        securityProvider.validateRequest()
        return userV2Service.updateUserV2Handle(request)
    }

    @RequestMapping(value = ["/updateNotificationToken"], method = [RequestMethod.POST])
    fun updateNotificationToken(@RequestBody request: UpdateNotificationTokenRequest): SavedUserV2Response? {
        securityProvider.validateRequest()
        return userV2Service.updateNotificationToken(request)
    }

    @RequestMapping(value = ["/updateUserV2Email"], method = [RequestMethod.POST])
    fun updateUserV2Email(@RequestBody request: UpdateUserV2EmailRequest): SavedUserV2Response? {
        securityProvider.validateRequest()
        return userV2Service.updateUserV2Email(request)
    }

    @RequestMapping(value = ["/removeUserV2DP"], method = [RequestMethod.POST])
    fun removeUserV2DP(): SavedUserV2Response? {
        securityProvider.validateRequest()
        return userV2Service.removeUserV2DP()
    }

    @RequestMapping(value = ["/removeUserV2Handle"], method = [RequestMethod.POST])
    fun removeUserV2Handle(): SavedUserV2Response? {
        securityProvider.validateRequest()
        return userV2Service.removeUserV2Handle()
    }

    @RequestMapping(value = ["/isUserHandleAvailable"], method = [RequestMethod.GET])
    fun isUserHandleAvailable(@RequestParam handle: String): UserHandleAvailabilityResponse {
        securityProvider.validateRequest()
        return userV2Service.isUserHandleAvailable(handle)
    }

    @RequestMapping(value = ["/updateUserV2BusinessDuringSignup"], method = [RequestMethod.POST])
    fun updateUserV2BusinessDuringSignup(@RequestBody request: UpdateUserV2BusinessSignupRequest): SavedUserV2Response? {
        securityProvider.validateRequest()
        return userV2Service.updateUserV2BusinessDuringSignup(request)
    }

    @RequestMapping(value = ["/updateUserV2DP"], method = [RequestMethod.POST])
    fun updateUserV2DP(@RequestBody request: UpdateUserV2DPRequest): SavedUserV2Response? {
        securityProvider.validateRequest()
        return userV2Service.updateUserV2DP(request)
    }

    @RequestMapping(value = ["/updateUserV2CoverImage"], method = [RequestMethod.POST])
    fun updateUserV2CoverImage(@RequestBody request: UpdateUserV2CoverImageRequest): SavedUserV2Response? {
        securityProvider.validateRequest()
        return userV2Service.updateUserV2CoverImage(request)
    }

    @RequestMapping(value = ["/updateUserV2Profiles"], method = [RequestMethod.POST])
    fun updateUserV2Profiles(@RequestBody request: UpdateUserV2ProfilesRequest): SavedUserV2Response? {
        securityProvider.validateRequest()
        return userV2Service.updateUserV2Profiles(request)
    }

    @RequestMapping(value = ["/updateUserV2PreferredCategories"], method = [RequestMethod.POST])
    fun updateUserV2PreferredCategories(@RequestBody request: UpdateUserV2PreferredCategoriesRequest): SavedUserV2Response? {
        securityProvider.validateRequest()
        return userV2Service.updateUserV2PreferredCategories(request)
    }

    @RequestMapping(value = ["/updateUserV2Name"], method = [RequestMethod.POST])
    fun updateUserV2Name(@RequestBody request: UpdateUserV2NameRequest): SavedUserV2Response? {
        securityProvider.validateRequest()
        return userV2Service.updateUserV2Name(request)
    }

    @RequestMapping(value = ["/updateUserV2DuringSignup"], method = [RequestMethod.POST])
    fun updateUserV2DuringSignup(@RequestBody request: UpdateUserV2DuringSignupRequest): SavedUserV2Response? {
        securityProvider.validateRequest()
        return userV2Service.updateUserV2DuringSignup(request)
    }

    @RequestMapping(value = ["/updateUserV2Location"], method = [RequestMethod.POST])
    fun updateUserV2Location(@RequestBody request: UpdateUserV2LocationRequest): SavedUserV2Response? {
        securityProvider.validateRequest()
        return userV2Service.updateUserV2Location(request)
    }

    @RequestMapping(value = ["/getProfileTypesByProfileCategory"], method = [RequestMethod.GET])
    fun getProfileTypesByProfileCategory(@RequestParam profileCategory: ProfileCategory): AllProfileTypeResponse? {
        securityProvider.validateRequest()
        return userV2Service.getProfileTypesByProfileCategory(profileCategory)
    }

    @RequestMapping(value = ["/getAllProfileTypes"], method = [RequestMethod.GET])
    fun getAllProfileTypes(): AllProfileTypeResponse? {
        securityProvider.validateRequest()
        return userV2Service.getAllProfileTypes()
    }

    @RequestMapping(value = ["/report"], method = [RequestMethod.POST])
    fun report(@RequestBody request: UserReportRequest): UserReportResponse? {
        return userV2Service.report(request)
    }

    @RequestMapping(value = ["/getAllReport"], method = [RequestMethod.GET])
    fun getAllReport(@RequestParam userId: String): AllUserReportResponse? {
        return userV2Service.getAllReport(userId)
    }
}
