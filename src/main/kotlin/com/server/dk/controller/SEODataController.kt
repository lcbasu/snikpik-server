package com.server.dk.controller

import com.server.dk.dto.SavedProductResponse
import com.server.dk.dto.ShopViewForCustomerResponse
import com.server.dk.service.SEODataService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

/**
 *
 * APIs that are serving data that can be Public for SEO
 *
 * */
@RestController
@RequestMapping("seoData")
class SEODataController {

    @Autowired
    private lateinit var seoDataService: SEODataService

    @RequestMapping(value = ["/getProductDetails/{productId}"], method = [RequestMethod.GET])
    fun getProductDetails(@PathVariable productId: String): SavedProductResponse {
        return seoDataService.getProductDetails(productId)
    }

    @RequestMapping(value = ["/getShopViewForCustomer/{username}"], method = [RequestMethod.GET])
    fun getShopViewForCustomer(@PathVariable username: String): ShopViewForCustomerResponse? {
        return seoDataService.getShopViewForCustomer(username)
    }
}
