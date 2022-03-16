package com.server.shop.provider

import com.server.common.enums.ReadableIdPrefix
import com.server.common.provider.SecurityProvider
import com.server.common.provider.UniqueIdProvider
import com.server.shop.dao.AddressV3Repository
import com.server.shop.dto.DeleteAddressV3Request
import com.server.shop.dto.SaveAddressV3Request
import com.server.shop.dto.UpdateAddressV3Request
import com.server.shop.dto.toSaveAddressV3Request
import com.server.shop.entities.AddressV3
import com.server.shop.entities.UserV3
import com.server.shop.enums.AddressPOCType
import com.server.shop.enums.AddressType
import com.server.ud.entities.location.getCompleteAddress
import com.server.ud.provider.location.LocationProvider
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class AddressV3Provider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var securityProvider: SecurityProvider

    @Autowired
    private lateinit var addressV3Repository: AddressV3Repository

    @Autowired
    private lateinit var locationProvider: LocationProvider

    @Autowired
    private lateinit var uniqueIdProvider: UniqueIdProvider

    @Autowired
    private lateinit var userV3Provider: UserV3Provider

    @Autowired
    private lateinit var userAddressV3Provider: UserAddressV3Provider

    fun getAddressV3(addressId: String): AddressV3? =
        try {
            addressV3Repository.findById(addressId).get()
        } catch (e: Exception) {
            e.printStackTrace()
            logger.error("Filed to get AddressV3 for addressId: $addressId")
            null
        }

    fun convertLocationToAddressV3(user: UserV3, locationId: String): AddressV3? {
        return try {
            val location = locationProvider.getLocation(locationId) ?: error("Failed to get location for locationId: $locationId")
            val request = SaveAddressV3Request(
                addressType = AddressType.OFFICE,
                pocName = user.fullName ?: "Guest User",
                pocType = AddressPOCType.PERSON,
                email = user.email ?: "",

                flatNoBuildingApartmentName = "${location.name}, ${location.route}",
                streetLocality = "${location.locality}, ${location.subLocality}, ${location.city}",
                zipcode = location.zipcode ?: "",

                absoluteMobile = user.absoluteMobile ?: "",
                countryCode = user.countryCode ?: "",

                route = location.route,
                locality = location.locality,
                subLocality = location.subLocality,
                city = location.city,
                state = location.state,
                country = location.country,
                googleCode = location.googlePlaceId,
                completeAddress = location.getCompleteAddress(),

                latitude = location.lat ?: 0.0,
                longitude = location.lng ?: 0.0,
            )
            saveAddress(user, request)
        } catch (e: Exception) {
            e.printStackTrace()
            logger.error("Filed to convert location to AddressV3 for locationId: $locationId")
            null
        }
    }

    fun saveAddress(userV3: UserV3, request: SaveAddressV3Request): AddressV3? {
        return try {
            val id = uniqueIdProvider.getUniqueIdAfterSaving(ReadableIdPrefix.ADR.name)
            saveAddress(userV3, id, request)
        } catch (e: Exception) {
            logger.error("Failed to save saveAddress")
            e.printStackTrace()
            null
        }
    }

    fun saveAddress(userV3: UserV3, id: String, request: SaveAddressV3Request): AddressV3? {
        return try {
            val newAddress = getAddressV3Object(id, request)
            saveAddress(userV3, id, newAddress)
        } catch (e: Exception) {
            logger.error("Failed to save saveAddress")
            e.printStackTrace()
            null
        }
    }

    fun saveAddress(userV3: UserV3, id: String, addressObject: AddressV3): AddressV3? {
        return try {
            addressObject.addedBy = userV3
            val savedAddress = addressV3Repository.save(addressObject)
            userAddressV3Provider.save(userV3, savedAddress)
            userV3Provider.saveDefaultAddress(userV3, savedAddress)
            savedAddress
        } catch (e: Exception) {
            logger.error("Failed to save saveAddress")
            e.printStackTrace()
            null
        }
    }

    fun getAddressV3Object(id: String, request: SaveAddressV3Request): AddressV3 {
        val newAddress = AddressV3()
        newAddress.id = id

        newAddress.addressType = request.addressType
        newAddress.pocName = request.pocName
        newAddress.pocType = request.pocType
        newAddress.email = request.email

        newAddress.flatNoBuildingApartmentName = request.flatNoBuildingApartmentName
        newAddress.streetLocality = request.streetLocality
        newAddress.zipcode = request.zipcode

        newAddress.absoluteMobile = request.absoluteMobile
        newAddress.countryCode = request.countryCode

        newAddress.route = request.route
        newAddress.locality = request.locality
        newAddress.subLocality = request.subLocality
        newAddress.city = request.city
        newAddress.state = request.state
        newAddress.country = request.country
        newAddress.googleCode = request.googleCode
        newAddress.completeAddress = request.completeAddress ?: ""

        newAddress.latitude = request.latitude ?: 0.0
        newAddress.longitude = request.longitude ?: 0.0
        return newAddress
    }

    fun save(request: SaveAddressV3Request): AddressV3? {
        val userV3 = userV3Provider.getUserV3FromLoggedInUser() ?: error("Logged in user not found")
        return saveAddress(userV3, request)
    }

    fun update(request: UpdateAddressV3Request): AddressV3? {
        val userV3 = userV3Provider.getUserV3FromLoggedInUser() ?: error("Logged in user not found")
        val addressV3 = getAddressV3(request.id) ?: error("Address not found for id: ${request.id}")
        if (addressV3.addedBy!!.id != userV3.id) {
            error("User is not authorized to edit address: ${request.id}")
        }
        return saveAddress(userV3, request.id, request.toSaveAddressV3Request())
    }

    fun delete(request: DeleteAddressV3Request): AddressV3? {
        val userV3 = userV3Provider.getUserV3FromLoggedInUser() ?: error("Logged in user not found")
        val addressV3 = getAddressV3(request.id) ?: error("Address not found for id: ${request.id}")
        if (addressV3.addedBy!!.id != userV3.id) {
            error("User is not authorized to delete address: ${request.id}")
        }
        addressV3.deleted = true
        return saveAddress(userV3, addressV3.id, addressV3)
    }

}
