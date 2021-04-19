package com.dukaankhata.server.utils

import com.dukaankhata.server.dao.AddressRepository
import com.dukaankhata.server.dao.CompanyAddressRepository
import com.dukaankhata.server.dao.UserAddressRepository
import com.dukaankhata.server.dto.SaveAddressRequest
import com.dukaankhata.server.entities.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class AddressUtils {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)
    @Autowired
    private lateinit var addressRepository: AddressRepository

    @Autowired
    private lateinit var companyAddressRepository: CompanyAddressRepository

    @Autowired
    private lateinit var userAddressRepository: UserAddressRepository

    fun saveAddress(saveAddressRequest: SaveAddressRequest): Address? {
        try {
            val newAddress = Address()
            newAddress.line1 = saveAddressRequest.line1
            newAddress.line2 = saveAddressRequest.line2
            newAddress.zipcode = saveAddressRequest.zipcode
            newAddress.city = saveAddressRequest.city
            newAddress.state = saveAddressRequest.state
            newAddress.country = saveAddressRequest.country
            newAddress.googleCode = saveAddressRequest.googleCode
            newAddress.latitude = saveAddressRequest.latitude
            newAddress.longitude = saveAddressRequest.longitude
            newAddress.phone = saveAddressRequest.phone
            return addressRepository.saveAndFlush(newAddress)
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
            return companyAddressRepository.saveAndFlush(companyAddress)
        } catch (e: Exception) {
            logger.error("Failed to save companyAddress")
            e.printStackTrace()
            return null
        }
    }

    fun saveUserAddress(user: User, name: String, saveAddressRequest: SaveAddressRequest): UserAddress? {
        try {
            val newAddress = saveAddress(saveAddressRequest) ?: error("Address should be saved")
            val userAddressKey = UserAddressKey()
            userAddressKey.addressId = newAddress.id
            userAddressKey.userId = user.id

            val userAddress = UserAddress()
            userAddress.id = userAddressKey
            userAddress.name = name
            userAddress.user = user
            userAddress.address = newAddress
            return userAddressRepository.saveAndFlush(userAddress)
        } catch (e: Exception) {
            logger.error("Failed to save userAddress")
            e.printStackTrace()
            return null
        }
    }
}
