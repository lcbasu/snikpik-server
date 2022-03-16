package com.server.shop.controller

import com.server.shop.dto.TaggedProductCommissionsResponse
import com.server.shop.service.ShopV3Service
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("shop/home/v3")
class ShopV3Controller {

    @Autowired
    private lateinit var shopV3Service: ShopV3Service
    @RequestMapping(value = ["/getTaggedProductCommissions"], method = [RequestMethod.GET])
    fun getTaggedProductCommissions(): TaggedProductCommissionsResponse {
        return TaggedProductCommissionsResponse(
            commissionPercentageOnProduct = 10.00,
            maxCommissionInPaisaOnProduct = 50000,

            unboxMarginInPercentage = 5.00,
        )
    }

}
