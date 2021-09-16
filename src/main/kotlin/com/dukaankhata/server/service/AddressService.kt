package com.dukaankhata.server.service

import com.dukaankhata.server.dto.*

abstract class AddressService {
    abstract fun save(saveAddressRequest: SaveAddressRequest): SavedAddressResponse
}
