package com.server.shop.service

import com.server.shop.dto.*
import com.server.shop.provider.AddressV3Provider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AddressV3ServiceImpl : AddressV3Service() {

    @Autowired
    private lateinit var addressV3Provider: AddressV3Provider

    override fun save(request: SaveAddressV3Request): SavedAddressV3Response? {
        return addressV3Provider.save(request)?.toSavedAddressV3Response()
    }

    override fun update(request: UpdateAddressV3Request): SavedAddressV3Response? {
        return addressV3Provider.update(request)?.toSavedAddressV3Response()
    }

    override fun delete(request: DeleteAddressV3Request): SavedAddressV3Response? {
        return addressV3Provider.delete(request)?.toSavedAddressV3Response()
    }

    override fun get(addressId: String): SavedAddressV3Response? {
        return addressV3Provider.getAddressV3(addressId)?.toSavedAddressV3Response()
    }

}
