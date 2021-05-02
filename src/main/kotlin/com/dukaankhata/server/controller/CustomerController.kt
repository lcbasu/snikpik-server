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
        return dkShopService.updateCart(updateCartRequest)
    }

    @RequestMapping(value = ["/migrateCart"], method = [RequestMethod.POST])
    fun migrateCart(@RequestBody migrateCartRequest: MigrateCartRequest): MigratedProductOrderResponse? {
        customerPreChecks()
        return dkShopService.migrateCart(migrateCartRequest)
    }

    @RequestMapping(value = ["/getActiveProductOrderBag/{shopUsername}"], method = [RequestMethod.GET])
    fun getActiveProductOrderBag(@PathVariable shopUsername: String): SavedProductOrderResponse? {
        customerPreChecks()
        return dkShopService.getActiveProductOrderBag(shopUsername)
    }

    @RequestMapping(value = ["/getProductOrder/{productOrderId}"], method = [RequestMethod.GET])
    fun getProductOrder(@PathVariable productOrderId: String): SavedProductOrderResponse {
        customerPreChecks()
        return dkShopService.getProductOrder(productOrderId)
    }

    @RequestMapping(value = ["/productOrderUpdateByCustomer"], method = [RequestMethod.POST])
    fun productOrderUpdateByCustomer(@RequestBody productOrderUpdateByCustomerRequest: ProductOrderUpdateByCustomerRequest): SavedProductOrderResponse {
        customerPreChecks()
        return dkShopService.productOrderUpdateByCustomer(productOrderUpdateByCustomerRequest)
    }

    @RequestMapping(value = ["/approveProductOrderUpdateByCustomer"], method = [RequestMethod.POST])
    fun approveProductOrderUpdateByCustomer(@RequestBody productOrderUpdateApprovalRequest: ProductOrderUpdateApprovalRequest): SavedProductOrderResponse {
        customerPreChecks()
        return dkShopService.approveProductOrderUpdateByCustomer(productOrderUpdateApprovalRequest)
    }

    @RequestMapping(value = ["/placeProductOrder"], method = [RequestMethod.POST])
    fun placeProductOrder(@RequestBody placeProductOrderRequest: PlaceProductOrderRequest): SavedProductOrderResponse {
        customerPreChecks()
        return dkShopService.placeProductOrder(placeProductOrderRequest)
    }

    private fun customerPreChecks() {
        authUtils.makeSureThePublicRequestHasUserEntity()
    }
}
