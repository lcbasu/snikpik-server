package com.server.shop.provider

import com.server.common.provider.SecurityProvider
import com.server.shop.dao.UserV3Repository
import com.server.shop.dto.UserV3AddressesResponse
import com.server.shop.dto.toSavedAddressV3Response
import com.server.shop.entities.AddressV3
import com.server.shop.entities.UserAddressV3
import com.server.shop.entities.UserV3
import com.server.shop.entities.toUserV2PublicMiniDataResponse
import com.server.ud.entities.user.UserV2
import com.server.ud.provider.user.UserV2Provider
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class UserV3Provider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var securityProvider: SecurityProvider

    @Autowired
    private lateinit var userV3Repository: UserV3Repository

    @Autowired
    private lateinit var userV2Provider: UserV2Provider

    @Autowired
    private lateinit var addressV3Provider: AddressV3Provider

    @Autowired
    private lateinit var userAddressV3Provider: UserAddressV3Provider

    @Autowired
    private lateinit var productV3Provider: ProductV3Provider

    fun getUserV3(userId: String): UserV3? =
        try {
            userV3Repository.findById(userId).get()
        } catch (e: Exception) {
            e.printStackTrace()
            logger.error("Failed to get UserV3 for userId: $userId")
            null
        }

    fun getUserV3FromLoggedInUser(): UserV3? =
        try {
            val userId = securityProvider.getFirebaseAuthUser()?.getUserIdToUse() ?: error("Failed to get userId")
            val userV3Optional = userV3Repository.findById(userId)
            if(userV3Optional.isPresent) {
                userV3Optional.get()
            } else {
                logger.warn("UserV3 for userId: $userId is not present. So trying to get UserV2 and saving that user as UserV3")
                val userV2 = userV2Provider.getUser(userId) ?: error("Failed to get UserV2 for userId: $userId")
                save(userV2)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            logger.error("Failed to get UserV3 for logged in user")
            null
        }

    fun save(userV2: UserV2): UserV3 {

        // Get already saved UserV3 if in case it exists
        val oldSavedUserV3 = getUserV3(userV2.userId)

        val oldPermanentLocationId = oldSavedUserV3?.permanentLocationId

        val newUser = oldSavedUserV3 ?: UserV3()
        newUser.id = userV2.userId

        newUser.absoluteMobile = userV2.absoluteMobile
        newUser.countryCode = userV2.countryCode

        newUser.handle = userV2.handle
        newUser.email = userV2.email
        newUser.dp = userV2.dp

        newUser.coverImage = userV2.coverImage
        newUser.uid = userV2.uid ?: ""
        newUser.anonymous = userV2.anonymous
        newUser.verified = userV2.verified
        newUser.profiles = userV2.profiles
        newUser.contactVisible = userV2.contactVisible

        newUser.fullName = userV2.fullName ?: ""

        newUser.notificationToken = userV2.notificationToken ?: ""

        newUser.notificationTokenProvider = userV2.notificationTokenProvider

        newUser.currentLocationZipcode = userV2.currentLocationZipcode
        newUser.currentGooglePlaceId = userV2.currentGooglePlaceId
        newUser.currentLocationId = userV2.currentLocationId
        newUser.currentLocationName = userV2.currentLocationName
        newUser.currentLocationLat = userV2.currentLocationLat
        newUser.currentLocationLng = userV2.currentLocationLng
        newUser.currentLocationLocality = userV2.currentLocationLocality
        newUser.currentLocationSubLocality = userV2.currentLocationSubLocality
        newUser.currentLocationRoute = userV2.currentLocationRoute
        newUser.currentLocationCity = userV2.currentLocationCity
        newUser.currentLocationState = userV2.currentLocationState
        newUser.currentLocationCountry = userV2.currentLocationCountry
        newUser.currentLocationCountryCode = userV2.currentLocationCountryCode
        newUser.currentLocationCompleteAddress = userV2.currentLocationCompleteAddress


        newUser.permanentLocationZipcode = userV2.permanentLocationZipcode
        newUser.permanentGooglePlaceId = userV2.permanentGooglePlaceId
        newUser.permanentLocationId = userV2.permanentLocationId
        newUser.permanentLocationName = userV2.permanentLocationName
        newUser.permanentLocationLat = userV2.permanentLocationLat
        newUser.permanentLocationLng = userV2.permanentLocationLng
        newUser.permanentLocationLocality = userV2.permanentLocationLocality
        newUser.permanentLocationSubLocality = userV2.permanentLocationSubLocality
        newUser.permanentLocationRoute = userV2.permanentLocationRoute
        newUser.permanentLocationCity = userV2.permanentLocationCity
        newUser.permanentLocationState = userV2.permanentLocationState
        newUser.permanentLocationCountry = userV2.permanentLocationCountry
        newUser.permanentLocationCountryCode = userV2.permanentLocationCountryCode
        newUser.permanentLocationCompleteAddress = userV2.permanentLocationCompleteAddress

        newUser.preferredCategories = userV2.preferredCategories

        val newSavedUserV3 = userV3Repository.save(newUser)

        // If they are both same then there is no need to create a new location address
        if (oldPermanentLocationId == userV2.permanentLocationId) {
            logger.warn("If they are both same then there is no need to create a new location address for userId: ${userV2.userId}")
        } else {
            newSavedUserV3.defaultAddress = userV2.permanentLocationId?.let { addressV3Provider.convertLocationToAddressV3(newSavedUserV3, it) }
        }

        return userV3Repository.save(newSavedUserV3)
    }


    fun saveDefaultAddress (userV3: UserV3, address: AddressV3): UserV3 {
        if (address.deleted) {
            userV3.defaultAddress = null
        } else {
            userV3.defaultAddress = address
        }
        return userV3Repository.save(userV3)
    }

    fun getUserV3Addresses(userId: String): List<UserAddressV3> {
        return userAddressV3Provider.getUserAddresses(userId)
    }

    fun getUserV3Addresses(): UserV3AddressesResponse {
        val userV3 = getUserV3FromLoggedInUser() ?: error("Logged in user not found")
        val userAddresses = getUserV3Addresses(userV3.id)

        val addresses = userAddresses.mapNotNull {
            it.address?.toSavedAddressV3Response()
        }
        return UserV3AddressesResponse(
            user = userV3.toUserV2PublicMiniDataResponse(),
            addresses = addresses
        )
    }

    fun getCreatorsInFocus(): List<UserV3> {
        return productV3Provider.getCreatorsInFocus()
    }
}
