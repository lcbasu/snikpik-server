package com.server.dk.service

import com.server.dk.dto.*

abstract class AddressService {
    abstract fun save(saveAddressRequest: SaveAddressRequest): SavedAddressResponse
}
