package com.server.ud.provider.user

import com.server.common.entities.User
import com.server.common.utils.DateUtils
import com.server.dk.model.convertToString
import com.server.ud.dao.user.UserV2Repository
import com.server.ud.dto.*
import com.server.ud.entities.user.UserV2
import com.server.ud.enums.LocationFor
import com.server.ud.provider.location.LocationProvider
import com.server.ud.service.user.ProcessUserV2SchedulerService
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
    private lateinit var processUserV2SchedulerService: ProcessUserV2SchedulerService

    @Autowired
    private lateinit var usersByHandleProvider: UsersByHandleProvider

    @Autowired
    private lateinit var locationProvider: LocationProvider

    fun getUser(userId: String): UserV2? =
        try {
            val users = userV2Repository.findAllByUserId(userId)
            if (users.size > 1) {
                error("More than one user has same userId: $userId")
            }
            users.firstOrNull()
        } catch (e: Exception) {
            logger.error("Getting User for $userId failed.")
            e.printStackTrace()
            null
        }

    fun saveUserV2(userV2: UserV2) : UserV2? {
        try {
            val savedUser = userV2Repository.save(userV2)
            logger.info("UserV2 saved with userId: ${savedUser.userId}.")
            processUserV2SchedulerService.createUserV2ProcessingJob(savedUser)
            return savedUser
        } catch (e: Exception) {
            logger.error("Saving UserV2 for ${userV2.userId} failed.")
            e.printStackTrace()
            return null
        }
    }

    fun saveDKUserToUD(user: User) : UserV2? {
        try {
            val userV2 = UserV2(
                userId = user.id,
                createdAt = DateUtils.toDate(user.createdAt).toInstant(),
                absoluteMobile = user.absoluteMobile,
                countryCode = user.countryCode,
                uid = user.uid,
                anonymous = user.anonymous,
                fullName = user.fullName,
                notificationToken = user.notificationToken,
                notificationTokenProvider = user.notificationTokenProvider,
            )
            logger.info("Completed")
            return saveUserV2(userV2)
        } catch (e: Exception) {
            logger.error("Saving UserV2 for ${user.id} failed.")
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

        val location = locationProvider.save(user, locationRequest) ?: error("Error saving location for userId: ${request.userId}")

        val newUserToBeSaved = user.copy(
            userLastLocationId = location.locationId,
            userLastLocationLat = location.lat,
            userLastLocationLng = location.lng,
            userLastLocationZipcode = location.zipcode,
            userLastLocationName = location.name,
            userLastGooglePlaceId = location.googlePlaceId)
        return saveUserV2(newUserToBeSaved)
    }

}
