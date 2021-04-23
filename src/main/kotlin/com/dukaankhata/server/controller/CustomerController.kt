package com.dukaankhata.server.controller

import com.dukaankhata.server.dto.*
import com.dukaankhata.server.service.DKShopService
import com.dukaankhata.server.utils.AuthUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

// ALL calls for customer will be routed here
// In the app, login all the people as Anonymous users the moment they visit the store
@RestController
@RequestMapping("customer")
class CustomerController {

    @Autowired
    private lateinit var authUtils: AuthUtils

    // IMPORTANT
    // To get single prodcut view,
    // we do not need to add a new API
    // we are already sending all the prodcu details
    // in getShopViewForCustomer api
    // So all we need is to show that details
    // along with that we can use getRelatedProducts
    // api to show related products

    @Autowired
    private lateinit var dkShopService: DKShopService

    @RequestMapping(value = ["/getShopViewForCustomer/{username}"], method = [RequestMethod.GET])
    fun getShopViewForCustomer(@PathVariable username: String): ShopViewForCustomerResponse? {
        customerPreChecks()
        return dkShopService.getShopViewForCustomer(username)
    }

    @RequestMapping(value = ["/getRelatedProducts/{productId}"], method = [RequestMethod.GET])
    fun getRelatedProducts(@PathVariable productId: String): RelatedProductsResponse? {
        customerPreChecks()
        return dkShopService.getRelatedProducts(productId)
    }

    @RequestMapping(value = ["/updateCart"], method = [RequestMethod.POST])
    fun updateCart(@RequestBody updateCartRequest: UpdateCartRequest): SavedProductOrderResponse? {
        customerPreChecks()
        return dkShopService.updateCartRequest(updateCartRequest)
    }

    @RequestMapping(value = ["/getActiveProductOrderBag/{shopUsername}"], method = [RequestMethod.GET])
    fun getActiveProductOrderBag(@PathVariable shopUsername: String): SavedProductOrderResponse? {
        customerPreChecks()
        return dkShopService.getActiveProductOrderBag(shopUsername)
    }

    private fun customerPreChecks() {
        authUtils.makeSureThePublicRequestHasUserEntity()
    }
}
