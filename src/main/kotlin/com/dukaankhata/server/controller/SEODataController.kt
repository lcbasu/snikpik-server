package com.dukaankhata.server.controller

import com.dukaankhata.server.dto.SavedProductResponse
import com.dukaankhata.server.service.SEODataService
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

}
