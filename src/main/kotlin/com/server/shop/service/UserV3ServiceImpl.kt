package com.server.shop.service

import com.server.shop.dto.AllCreatorsResponse
import com.server.shop.dto.UserV3AddressesResponse
import com.server.shop.entities.toUserV2PublicMiniDataResponse
import com.server.shop.provider.UserV3Provider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserV3ServiceImpl : UserV3Service() {

    @Autowired
    private lateinit var userV3Provider: UserV3Provider

    override fun getUserV3Addresses(): UserV3AddressesResponse {
        return userV3Provider.getUserV3Addresses()
    }

    override fun getCreatorsInFocus(): AllCreatorsResponse {
        return AllCreatorsResponse(userV3Provider.getCreatorsInFocus().map { it.toUserV2PublicMiniDataResponse() })
    }

}
