package com.server.shop.service

import com.server.shop.dto.DeleteAddressV3Request
import com.server.shop.dto.SaveAddressV3Request
import com.server.shop.dto.SavedAddressV3Response
import com.server.shop.dto.UpdateAddressV3Request

abstract class AddressV3Service {
    abstract fun save(request: SaveAddressV3Request): SavedAddressV3Response?
    abstract fun update(request: UpdateAddressV3Request): SavedAddressV3Response?
    abstract fun delete(request: DeleteAddressV3Request): SavedAddressV3Response?
    abstract fun get(addressId: String): SavedAddressV3Response?
}
