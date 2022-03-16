package com.server.shop.service

import com.server.shop.dto.AllCreatorsResponse
import com.server.shop.dto.UserV3AddressesResponse

abstract class UserV3Service {
    abstract fun getUserV3Addresses(): UserV3AddressesResponse
    abstract fun getCreatorsInFocus(): AllCreatorsResponse
}
