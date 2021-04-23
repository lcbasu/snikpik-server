package com.dukaankhata.server.controller

import com.dukaankhata.server.dto.*
import com.dukaankhata.server.service.DKShopService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

// ALL calls for customer will be routed here
// In the app, login all the people as Anonymous users the moment they visit the store
@RestController
@RequestMapping("customer")
class CustomerController {

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

    // Create a account for non-logged in user
    @RequestMapping(value = ["/handleNonLoggedInUser"], method = [RequestMethod.POST])
    fun handleNonLoggedInUser(): SavedUserResponse? {
        return dkShopService.handleNonLoggedInUser()
    }

    @RequestMapping(value = ["/getShopViewForCustomer/{username}"], method = [RequestMethod.GET])
    fun getShopViewForCustomer(@PathVariable username: String): ShopViewForCustomerResponse? {
        return dkShopService.getShopViewForCustomer(username)
    }

    @RequestMapping(value = ["/getRelatedProducts/{productId}"], method = [RequestMethod.GET])
    fun getRelatedProducts(@PathVariable productId: String): RelatedProductsResponse? {
        return dkShopService.getRelatedProducts(productId)
    }

    @RequestMapping(value = ["/updateCart"], method = [RequestMethod.GET])
    fun updateCart(@RequestBody updateCartRequest: UpdateCartRequest): SavedProductOrderResponse? {
        return dkShopService.updateCartRequest(updateCartRequest)
    }
}
