package com.dukaankhata.server.service.impl

import com.dukaankhata.server.dto.SaveAddressRequest
import com.dukaankhata.server.dto.SavedAddressResponse
import com.dukaankhata.server.dto.toSavedAddressResponse
import com.dukaankhata.server.provider.AddressProvider
import com.dukaankhata.server.provider.AuthProvider
import com.dukaankhata.server.service.AddressService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AddressServiceImpl : AddressService() {

    @Autowired
    private lateinit var authProvider: AuthProvider

    @Autowired
    private lateinit var addressProvider: AddressProvider

    override fun save(saveAddressRequest: SaveAddressRequest): SavedAddressResponse {
        val requestContext = authProvider.validateRequest()
        val user = requestContext.user
        val userAddress = addressProvider.saveUserAddress(user, saveAddressRequest) ?: error("Error while saving user address")
        val newAddress = userAddress.address ?: error("Address should always be present for userAddress")
        authProvider.updateUserDefaultAddress(user, newAddress) ?: error("Error while updating default address for user")

        return newAddress.toSavedAddressResponse()
    }

}
