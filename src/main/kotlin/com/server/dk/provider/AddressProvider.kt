package com.server.dk.provider

import com.server.common.provider.UniqueIdProvider
import com.server.dk.dao.AddressRepository
import com.server.dk.dao.CompanyAddressRepository
import com.server.dk.dao.UserAddressRepository
import com.server.dk.dto.SaveAddressRequest
import com.server.dk.entities.*
import com.server.common.enums.ReadableIdPrefix
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class AddressProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)
    @Autowired
    private lateinit var addressRepository: AddressRepository

    @Autowired
    private lateinit var companyAddressRepository: CompanyAddressRepository

    @Autowired
    private lateinit var userAddressRepository: UserAddressRepository

    @Autowired
    private lateinit var uniqueIdProvider: UniqueIdProvider

    fun getAddress(addressId: String): Address? =
        try {
            addressRepository.findById(addressId).get()
        } catch (e: Exception) {
            null
        }

    // TODO: Update the model
    fun saveAddress(saveAddressRequest: SaveAddressRequest): Address? {
        try {
            val newAddress = Address()
            newAddress.id = uniqueIdProvider.getUniqueId(ReadableIdPrefix.ADR.name)
            newAddress.line1 = saveAddressRequest.house
            newAddress.line2 = saveAddressRequest.roadName
            newAddress.zipcode = saveAddressRequest.zipcode
            newAddress.city = saveAddressRequest.city
            newAddress.state = saveAddressRequest.state
            newAddress.country = saveAddressRequest.country
            newAddress.googleCode = saveAddressRequest.googleCode
            newAddress.latitude = saveAddressRequest.latitude ?: 0.0
            newAddress.longitude = saveAddressRequest.longitude ?: 0.0
            newAddress.absoluteMobile = saveAddressRequest.absoluteMobile
            newAddress.countryCode = saveAddressRequest.countryCode
            return addressRepository.save(newAddress)
        } catch (e: Exception) {
            logger.error("Failed to save saveAddress")
            e.printStackTrace()
            return null
        }
    }

    fun saveCompanyAddress(company: Company, name: String, saveAddressRequest: SaveAddressRequest): CompanyAddress? {
        try {
            val newAddress = saveAddress(saveAddressRequest) ?: error("Address should be saved")
            val companyAddressKey = CompanyAddressKey()
            companyAddressKey.addressId = newAddress.id
            companyAddressKey.companyId = company.id

            val companyAddress = CompanyAddress()
            companyAddress.id = companyAddressKey
            companyAddress.name = name
            companyAddress.company = company
            companyAddress.address = newAddress
            return companyAddressRepository.save(companyAddress)
        } catch (e: Exception) {
            logger.error("Failed to save companyAddress")
            e.printStackTrace()
            return null
        }
    }

    fun saveUserAddress(user: User, saveAddressRequest: SaveAddressRequest): UserAddress? {
        return try {
            val newAddress = saveAddress(saveAddressRequest) ?: error("Address should be saved")
            val userAddress = UserAddress()
            userAddress.id = getUserAddressKey(userId = user.id, addressId = newAddress.id)
            userAddress.name = saveAddressRequest.name
            userAddress.user = user
            userAddress.address = newAddress
            userAddressRepository.save(userAddress)
        } catch (e: Exception) {
            logger.error("Failed to save userAddress")
            e.printStackTrace()
            null
        }
    }

    fun getUserAddressKey(userId: String, addressId: String): UserAddressKey {
        val key = UserAddressKey()
        key.userId = userId
        key.addressId = addressId
        return key
    }

    fun getUserAddresses(user: User) = userAddressRepository.findAllByUser(user)
    fun getIsUserAddressValid(user: User, address: Address) = userAddressRepository.existsById(getUserAddressKey(userId = user.id, addressId = address.id))
}
