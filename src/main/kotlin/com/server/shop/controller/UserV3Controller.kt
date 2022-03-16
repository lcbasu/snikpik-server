package com.server.shop.controller

import com.server.shop.dto.*
import com.server.shop.service.UserV3Service
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("shop/user/v3")
class UserV3Controller {

    @Autowired
    private lateinit var userV3Service: UserV3Service

    @RequestMapping(value = ["/getUserV3Addresses"], method = [RequestMethod.GET])
    fun getUserV3Addresses(): UserV3AddressesResponse {
        return userV3Service.getUserV3Addresses()
    }

    @RequestMapping(value = ["/getCreatorsInFocus"], method = [RequestMethod.GET])
    fun getCreatorsInFocus(): AllCreatorsResponse {
        return userV3Service.getCreatorsInFocus()
    }
}
