package com.server.ud.provider.user

import com.server.common.enums.MediaType
import com.server.common.enums.NotificationTokenProvider
import com.server.common.enums.ProfileType
import com.server.common.enums.ReadableIdPrefix
import com.server.common.provider.SecurityProvider
import com.server.common.utils.DateUtils
import com.server.dk.model.MediaDetailsV2
import com.server.dk.model.SingleMediaDetail
import com.server.dk.model.convertToString
import com.server.ud.dao.user.UserV2Repository
import com.server.ud.dto.*
import com.server.ud.entities.user.UserV2
import com.server.ud.enums.LocationFor
import com.server.ud.provider.job.JobProvider
import com.server.ud.provider.location.LocationProvider
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class UserV2Provider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var userV2Repository: UserV2Repository

    @Autowired
    private lateinit var jobProvider: JobProvider

    @Autowired
    private lateinit var usersByHandleProvider: UsersByHandleProvider

    @Autowired
    private lateinit var locationProvider: LocationProvider

    @Autowired
    private lateinit var securityProvider: SecurityProvider

    fun getUser(userId: String): UserV2? =
        try {
            val userIdToFind = if (userId.startsWith(ReadableIdPrefix.USR.name)) userId else "${ReadableIdPrefix.USR.name}$userId"
            val users = userV2Repository.findAllByUserId(userIdToFind)
            if (users.size > 1) {
                error("More than one user has same userId: $userIdToFind")
            }
            users.firstOrNull()
        } catch (e: Exception) {
            logger.error("Getting User for $userId failed.")
            e.printStackTrace()
            null
        }

    fun saveUserV2(userV2: UserV2, scheduleJob: Boolean = true) : UserV2? {
        try {
            getUser(userV2.userId)?.let {
                logger.error("User already exists for ${userV2.userId}")
                return it
            }
            val savedUser = userV2Repository.save(userV2)
            logger.info("UserV2 saved with userId: ${savedUser.userId}.")
            if (scheduleJob) {
                jobProvider.scheduleProcessingForUserV2(savedUser.userId)
            }
            return savedUser
        } catch (e: Exception) {
            logger.error("Saving UserV2 for ${userV2.userId} failed.")
            e.printStackTrace()
            return null
        }
    }

    fun updateUserV2Handle(request: UpdateUserV2HandleRequest): UserV2? {
        val user = getUser(request.userId) ?: error("No user found for userId: ${request.userId}")
        if (usersByHandleProvider.isHandleAvailable(request.newHandle)) {
            val newUserToBeSaved = user.copy(handle = request.newHandle)
            val savedUser = saveUserV2(newUserToBeSaved)
            usersByHandleProvider.save(savedUser!!)
            return savedUser
        } else {
            error("${request.newHandle} not available")
        }
    }

    fun updateUserV2DP(request: UpdateUserV2DPRequest): UserV2? {
        val user = getUser(request.userId) ?: error("No user found for userId: ${request.userId}")
        val newUserToBeSaved = user.copy(dp = request.dp.convertToString())
        return saveUserV2(newUserToBeSaved)
    }

    fun updateUserV2Profiles(request: UpdateUserV2ProfilesRequest): UserV2? {
        val user = getUser(request.userId) ?: error("No user found for userId: ${request.userId}")
        val newUserToBeSaved = user.copy(profiles = request.profiles.joinToString(","))
        return saveUserV2(newUserToBeSaved)
    }

    fun updateUserV2Name(request: UpdateUserV2NameRequest): UserV2? {
        val user = getUser(request.userId) ?: error("No user found for userId: ${request.userId}")
        val newUserToBeSaved = user.copy(fullName = request.newName)
        return saveUserV2(newUserToBeSaved)
    }

    fun updateUserV2Location(request: UpdateUserV2LocationRequest): UserV2? {
        val user = getUser(request.userId) ?: error("No user found for userId: ${request.userId}")

        val locationRequest = SaveLocationRequest(
            locationFor = LocationFor.USER,
            zipcode = request.zipcode,
            googlePlaceId = request.googlePlaceId,
            name = request.name,
            lat = request.lat,
            lng = request.lng,
        )

        val location = locationProvider.save(user.userId, locationRequest) ?: error("Error saving location for userId: ${request.userId}")

        val newUserToBeSaved = user.copy(
            userLastLocationId = location.locationId,
            userLastLocationLat = location.lat,
            userLastLocationLng = location.lng,
            userLastLocationZipcode = location.zipcode,
            userLastLocationName = location.name,
            userLastGooglePlaceId = location.googlePlaceId)
        return saveUserV2(newUserToBeSaved)
    }

    fun saveUserV2(): UserV2? {
        val firebaseAuthUser = securityProvider.validateRequest()
        return saveUserV2(UserV2 (
            userId = firebaseAuthUser.getUserIdToUse(),
            createdAt = DateUtils.getInstantNow(),
            absoluteMobile = firebaseAuthUser.getAbsoluteMobileNumber(),
            countryCode = "",
            handle = firebaseAuthUser.getHandle(),
            email = firebaseAuthUser.getEmail(),
            dp = firebaseAuthUser.getPicture()?.let { MediaDetailsV2(listOf(
                SingleMediaDetail(
                    mediaUrl = it,
                    mediaType = MediaType.IMAGE,
                )
            )).convertToString() },
            uid = "firebaseAuthUser.getUid()",
            anonymous = firebaseAuthUser.getIsAnonymous() == true,
            verified = false,
            profiles = emptyList<ProfileType>().joinToString(","),
            fullName = firebaseAuthUser.getName(),
            notificationToken = null,
            notificationTokenProvider = NotificationTokenProvider.FIREBASE
        ))
    }

}
