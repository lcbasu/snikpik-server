package com.server.dk.controller

import com.server.dk.dto.*
import com.server.common.provider.AuthProvider
import com.server.dk.service.CustomerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

// ALL calls for customer will be routed here
// In the app, login all the people as Anonymous users the moment they visit the store

// ANONYMOUS or Logged in through Mobile both APIs
@RestController
@RequestMapping("customer")
class CustomerController {

    @Autowired
    private lateinit var authProvider: AuthProvider

    // IMPORTANT
    // To get single prodcut view,
    // we do not need to add a new API
    // we are already sending all the prodcu details
    // in getShopViewForCustomer api
    // So all we need is to show that details
    // along with that we can use getRelatedProducts
    // api to show related products

    @Autowired
    private lateinit var customerService: CustomerService

    @RequestMapping(value = ["/getShopViewForCustomer/{username}"], method = [RequestMethod.GET])
    fun getShopViewForCustomer(@PathVariable username: String): ShopViewForCustomerResponse? {
        customerPreChecks()
        return customerService.getShopViewForCustomer(username)
    }

    @RequestMapping(value = ["/getRelatedProducts/{productId}"], method = [RequestMethod.GET])
    fun getRelatedProducts(@PathVariable productId: String): RelatedProductsResponse? {
        customerPreChecks()
        return customerService.getRelatedProducts(productId)
    }

    @RequestMapping(value = ["/updateCart"], method = [RequestMethod.POST])
    fun updateCart(@RequestBody updateCartRequest: UpdateCartRequest): SavedProductOrderResponse? {
        customerPreChecks()
        return customerService.updateCart(updateCartRequest)
    }

    @RequestMapping(value = ["/migrateCart"], method = [RequestMethod.POST])
    fun migrateCart(@RequestBody migrateCartRequest: MigrateCartRequest): MigratedProductOrderResponse? {
        customerPreChecks()
        return customerService.migrateCart(migrateCartRequest)
    }

    @RequestMapping(value = ["/getActiveProductOrderBag/{shopUsername}"], method = [RequestMethod.GET])
    fun getActiveProductOrderBag(@PathVariable shopUsername: String): SavedProductOrderResponse? {
        customerPreChecks()
        return customerService.getActiveProductOrderBag(shopUsername)
    }

    @RequestMapping(value = ["/getProductOrder/{productOrderId}"], method = [RequestMethod.GET])
    fun getProductOrder(@PathVariable productOrderId: String): SavedProductOrderResponse {
        customerPreChecks()
        return customerService.getProductOrder(productOrderId)
    }

    @RequestMapping(value = ["/getProductOrders"], method = [RequestMethod.GET])
    fun getProductOrders(): AllProductOrdersResponse {
        customerPreChecks()
        return customerService.getProductOrders()
    }

    @RequestMapping(value = ["/getProductDetails/{productId}"], method = [RequestMethod.GET])
    fun getProductDetails(@PathVariable productId: String): SavedProductResponse {
        customerPreChecks()
        return customerService.getProductDetails(productId)
    }

    @RequestMapping(value = ["/getCollectionWithProducts/{collectionId}"], method = [RequestMethod.GET])
    fun getCollectionWithProducts(@PathVariable collectionId: String): CollectionWithProductsResponse {
        customerPreChecks()
        return customerService.getCollectionWithProducts(collectionId)
    }

    private fun customerPreChecks() {
        authProvider.makeSureThePublicRequestHasUserEntity()
    }
}
