package com.server.ud.provider.user

import com.server.common.dto.AllProfileTypeResponse
import com.server.common.dto.convertToString
import com.server.common.dto.toProfileTypeResponse
import com.server.common.enums.*
import com.server.common.model.MediaDetailsV2
import com.server.common.model.SingleMediaDetail
import com.server.common.model.UserDetailsFromToken
import com.server.common.model.convertToString
import com.server.common.provider.SecurityProvider
import com.server.common.utils.DateUtils
import com.server.ud.dao.user.UserV2Repository
import com.server.ud.dto.*
import com.server.ud.entities.user.UserV2
import com.server.ud.enums.LocationFor
import com.server.ud.enums.UserLocationUpdateType
import com.server.ud.provider.deferred.DeferredProcessingProvider
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
    private lateinit var deferredProcessingProvider: DeferredProcessingProvider

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
            val savedUser = userV2Repository.save(userV2)
            logger.info("UserV2 saved with userId: ${savedUser.userId}.")
            if (scheduleJob) {
                deferredProcessingProvider.deferProcessingForUserV2(savedUser.userId)
            }
            return savedUser
        } catch (e: Exception) {
            logger.error("Saving UserV2 for ${userV2.userId} failed.")
            e.printStackTrace()
            return null
        }
    }

    fun updateUserV2Handle(request: UpdateUserV2HandleRequest): UserV2? {
        val firebaseAuthUser = securityProvider.validateRequest()
        val user = getUser(firebaseAuthUser.getUserIdToUse()) ?: error("No user found for userId: ${firebaseAuthUser.getUserIdToUse()}")
        return updateUserV2Handle(user, request.newHandle)
    }

    fun updateUserV2Handle(user: UserV2, newHandle: String): UserV2? {
        if (usersByHandleProvider.isHandleAvailable(newHandle)) {
            val newUserToBeSaved = user.copy(handle = newHandle)
            val savedUser = saveUserV2(newUserToBeSaved)
            usersByHandleProvider.save(savedUser!!)
            return savedUser
        } else {
            error("$newHandle not available for userId: ${user.userId}")
        }
    }

    fun updateUserV2DP(request: UpdateUserV2DPRequest): UserV2? {
        val firebaseAuthUser = securityProvider.validateRequest()
        val user = getUser(firebaseAuthUser.getUserIdToUse()) ?: error("No user found for userId: ${firebaseAuthUser.getUserIdToUse()}")
        val newUserToBeSaved = user.copy(dp = request.dp.convertToString())
        return saveUserV2(newUserToBeSaved)
    }

    fun updateUserV2Profiles(request: UpdateUserV2ProfilesRequest): UserV2? {
        val firebaseAuthUser = securityProvider.validateRequest()
        val user = getUser(firebaseAuthUser.getUserIdToUse()) ?: error("No user found for userId: ${firebaseAuthUser.getUserIdToUse()}")
        val newUserToBeSaved = user.copy(profiles = AllProfileTypeResponse(request.profiles.map { it.toProfileTypeResponse() }).convertToString())
        return saveUserV2(newUserToBeSaved)
    }

    fun updateUserV2Name(request: UpdateUserV2NameRequest): UserV2? {
        val firebaseAuthUser = securityProvider.validateRequest()
        val user = getUser(firebaseAuthUser.getUserIdToUse()) ?: error("No user found for userId: ${firebaseAuthUser.getUserIdToUse()}")
        val newUserToBeSaved = user.copy(fullName = request.newName)
        return saveUserV2(newUserToBeSaved)
    }

    // Adding user id filed to handle Faker
    // Figure out a better solve without exposing the user id
    fun updateUserV2Location(request: UpdateUserV2LocationRequest, userId: String): UserV2? {
        val user = getUser(userId) ?: error("No user found for userId: $userId")
        return try {
            val locationRequest = SaveLocationRequest(
                locationFor = LocationFor.USER,
                zipcode = request.zipcode,
                googlePlaceId = request.googlePlaceId,
                name = request.name,
                lat = request.lat,
                lng = request.lng,
            )

            val location = locationProvider.save(user.userId, locationRequest) ?: error("Error saving location for userId: $userId")

            var newUserToBeSaved = if (request.updateTypes.contains(UserLocationUpdateType.CURRENT)) {
                user.copy(
                    currentLocationId = location.locationId,
                    currentLocationLat = location.lat,
                    currentLocationLng = location.lng,
                    currentLocationZipcode = location.zipcode,
                    currentLocationName = location.name,
                    currentGooglePlaceId = location.googlePlaceId,
                )
            } else {
                user
            }
            newUserToBeSaved = if (request.updateTypes.contains(UserLocationUpdateType.PERMANENT)) {
                newUserToBeSaved.copy(
                    permanentLocationId = location.locationId,
                    permanentLocationLat = location.lat,
                    permanentLocationLng = location.lng,
                    permanentLocationZipcode = location.zipcode,
                    permanentLocationName = location.name,
                    permanentGooglePlaceId = location.googlePlaceId,
                )
            } else {
                newUserToBeSaved
            }
            saveUserV2(newUserToBeSaved)
        } catch (e: Exception) {
            e.printStackTrace()
            logger.error("Error while updating user location for userId: $userId")
            user
        }
    }

    fun getLoggedInUserV2(): UserV2? {
        val firebaseAuthUser = securityProvider.validateRequest()
        return getUser(firebaseAuthUser.getUserIdToUse())
    }

    fun saveUserV2WhoJustLoggedIn(): UserV2? {
        val firebaseAuthUser = securityProvider.validateRequest()
        val existing = getUser(firebaseAuthUser.getUserIdToUse())
        if (existing != null) {
            return existing
        }
        return saveUserV2(getUserV2ObjectFromFirebaseObject(firebaseAuthUser))
    }

    fun getAWSLambdaAuthDetails(): AWSLambdaAuthResponse? {
        val userDetails = securityProvider.validateRequest()
        userDetails.getUserIdToUse()
        val userV2 = getUser(userDetails.getUserIdToUse()) ?: error("No user found with userId: ${userDetails.getUserIdToUse()}")
        return AWSLambdaAuthResponse(
            userId = userV2.userId,
            anonymous = userV2.anonymous
        )
    }

    fun saveLoggedInUserV2WithIPLocation(request: IPLocationData?): UserV2? {
        val hasLocationInfo = request?.zipcode != null &&
                request.zipcode.isNotBlank() &&
                request.latitude != null &&
                request.longitude != null

        return if (hasLocationInfo) {
            val firebaseAuthUser = securityProvider.validateRequest()
            val userId = firebaseAuthUser.getUserIdToUse()
            val existing = getUser(userId)

            // If existing user already has a location set,
            // Then update only the current location and NEVER the permanent location
            // If not set, then save both the current and permanent location as the
            // user is a new user
            val hasPermanentLocation = existing?.permanentLocationZipcode != null

            // Save location
            val ipAddressLocation = request!!
            val locationRequest = SaveLocationRequest(
                locationFor = LocationFor.USER,
                zipcode = ipAddressLocation.zipcode,
                googlePlaceId = null,
                name = "${ipAddressLocation.city}, ${ipAddressLocation.state}",
                lat = ipAddressLocation.latitude,
                lng = ipAddressLocation.longitude,
            )
            val location = locationProvider.save(userId, locationRequest) ?: error("Error saving location for userId: $userId")

            // Save User
            val userToBeSavedDraft = existing ?: getUserV2ObjectFromFirebaseObject(firebaseAuthUser)

            val userToBeSaved = userToBeSavedDraft.copy(
                currentLocationId = location.locationId,
                currentLocationLat = location.lat,
                currentLocationLng = location.lng,
                currentLocationZipcode = location.zipcode,
                currentLocationName = location.name,
                currentGooglePlaceId = location.googlePlaceId,

                permanentLocationId = if (hasPermanentLocation) userToBeSavedDraft.permanentLocationId else location.locationId,
                permanentLocationLat = if (hasPermanentLocation) userToBeSavedDraft.permanentLocationLat else location.lat,
                permanentLocationLng = if (hasPermanentLocation) userToBeSavedDraft.permanentLocationLng else location.lng,
                permanentLocationZipcode = if (hasPermanentLocation) userToBeSavedDraft.permanentLocationZipcode else location.zipcode,
                permanentLocationName = if (hasPermanentLocation) userToBeSavedDraft.permanentLocationName else location.name,
                permanentGooglePlaceId = if (hasPermanentLocation) userToBeSavedDraft.permanentGooglePlaceId else location.googlePlaceId,

                anonymous = firebaseAuthUser.getIsAnonymous() == true,
                absoluteMobile = firebaseAuthUser.getAbsoluteMobileNumber()
            )
            return saveUserV2(userToBeSaved) ?: error("Error while saving user with ip address for userId: $userId")
        } else {
            saveUserV2WhoJustLoggedIn()
        }
    }

    private fun getUserV2ObjectFromFirebaseObject(firebaseAuthUser: UserDetailsFromToken): UserV2 {
        var userName = firebaseAuthUser.getName()
        if (userName == null || userName.isNullOrBlank()) {
            userName = "Guest User"
        }
        return UserV2(
            userId = firebaseAuthUser.getUserIdToUse(),
            createdAt = DateUtils.getInstantNow(),
            absoluteMobile = firebaseAuthUser.getAbsoluteMobileNumber(),
            countryCode = "",
            handle = "",
            email = firebaseAuthUser.getEmail(),
            dp = firebaseAuthUser.getPicture()?.let {
                MediaDetailsV2(
                    listOf(
                        SingleMediaDetail(
                            mediaUrl = it,
                            mediaType = MediaType.IMAGE,
                        )
                    )
                ).convertToString()
            },
            uid = firebaseAuthUser.getUid(),
            anonymous = firebaseAuthUser.getIsAnonymous() == true,
            verified = false,
            fullName = userName,
            notificationToken = null,
            notificationTokenProvider = NotificationTokenProvider.FIREBASE,
            profiles = AllProfileTypeResponse(
                listOf(ProfileType.HOME_OWNER.toProfileTypeResponse())
            ).convertToString(),
        )
    }

    fun isUserHandleAvailable(handle: String): Boolean {
        return usersByHandleProvider.isHandleAvailable(handle)
    }

    fun updateUserV2DuringSignup(request: UpdateUserV2DuringSignupRequest): UserV2? {
        val firebaseAuthUser = securityProvider.validateRequest()
        val user = getUser(firebaseAuthUser.getUserIdToUse()) ?: error("No user found for userId: ${firebaseAuthUser.getUserIdToUse()}")
        // First just update the name and Profile Image
        // because there is no need for uniqueness enforcement for name and dp
        val newUserToBeSaved = user.copy(
            fullName = request.newName,
            dp = request.dp?.convertToString())

        val nameUpdatedUser = saveUserV2(newUserToBeSaved, false) ?: error("Error while updating name and dp for userId: ${firebaseAuthUser.getUserIdToUse()}")

        // Now update the username as it requires uniqueness enforcement
        // and once, updated, do user level processing by job scheduling
        return updateUserV2Handle(nameUpdatedUser, request.newHandle)
    }

    fun updateUserV2BusinessDuringSignup(request: UpdateUserV2BusinessSignupRequest): UserV2? {
        val firebaseAuthUser = securityProvider.validateRequest()
        val user = getUser(firebaseAuthUser.getUserIdToUse()) ?: error("No user found for userId: ${firebaseAuthUser.getUserIdToUse()}")
        val newUserToBeSaved = user.copy(email = request.email,)
        val emailSavedUser = saveUserV2(newUserToBeSaved, false) ?: error("Error while updating email for userId: ${firebaseAuthUser.getUserIdToUse()}")
        return request.location?.let { updateUserV2Location(request.location, emailSavedUser.userId) } ?: emailSavedUser
    }

    fun updateUserV2PreferredCategories(request: UpdateUserV2PreferredCategoriesRequest): UserV2? {
        val firebaseAuthUser = securityProvider.validateRequest()
        val user = getUser(firebaseAuthUser.getUserIdToUse()) ?: error("No user found for userId: ${firebaseAuthUser.getUserIdToUse()}")
        val newUserToBeSaved = user.copy(preferredCategories = AllCategoryV2Response(
            request.categories.map { it.toCategoryV2Response() }
        ).convertToString(),)
        return saveUserV2(newUserToBeSaved) ?: error("Error while updating categories for userId: ${firebaseAuthUser.getUserIdToUse()}")
    }

    fun getProfileTypesByProfileCategory(profileCategory: ProfileCategory): AllProfileTypeResponse? {
        return AllProfileTypeResponse(
            ProfileType.values().filter { it.category == profileCategory }.map {
                it.toProfileTypeResponse()
            }
        )
    }

    fun getAllProfileTypes(): AllProfileTypeResponse? {
        return AllProfileTypeResponse(
            ProfileType.values().map {
                it.toProfileTypeResponse()
            }
        )
    }

}
