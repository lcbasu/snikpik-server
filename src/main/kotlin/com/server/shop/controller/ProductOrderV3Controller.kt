package com.server.shop.controller

import com.server.shop.dto.*
import com.server.shop.service.ProductOrderV3Service
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

// ONLY Authenticated with Phone number APIs
@RestController
@RequestMapping("shop/productOrder/v3")
class ProductOrderV3Controller {

    @Autowired
    private lateinit var productOrderV3Service: ProductOrderV3Service

    @RequestMapping(value = ["/updateCart"], method = [RequestMethod.POST])
    fun updateCart(@RequestBody request: UpdateCartV3Request): SavedProductOrderV3Response? {
        return productOrderV3Service.updateCart(request)
    }

    @RequestMapping(value = ["/saveForLater"], method = [RequestMethod.POST])
    fun saveForLater(@RequestBody request: SaveForLaterRequest): SavedProductOrderV3Response? {
        return productOrderV3Service.saveForLater(request)
    }

    @RequestMapping(value = ["/updateDeliveryAddress"], method = [RequestMethod.POST])
    fun updateDeliveryAddress(@RequestBody request: UpdateDeliveryAddressRequest): SavedProductOrderV3Response? {
        return productOrderV3Service.updateDeliveryAddress(request)
    }

    @RequestMapping(value = ["/updateStatus"], method = [RequestMethod.POST])
    fun updateStatus(@RequestBody request: ProductOrderStatusUpdateV3Request): SavedProductOrderV3Response? {
        return productOrderV3Service.updateStatus(request)
    }

    @RequestMapping(value = ["/getActiveProductOrderBag"], method = [RequestMethod.GET])
    fun getActiveProductOrderBag(): SavedProductOrderV3Response? {
        return productOrderV3Service.getActiveProductOrderBag()
    }

    @RequestMapping(value = ["/getAllOrdersForLoggedInUser"], method = [RequestMethod.GET])
    fun getAllOrdersForLoggedInUser(): AllProductOrderV3Response? {
        return productOrderV3Service.getAllOrdersForLoggedInUser()
    }

    @RequestMapping(value = ["/clearCart"], method = [RequestMethod.POST])
    fun clearCart(): SavedProductOrderV3Response? {
        return productOrderV3Service.clearCart()
    }

    @RequestMapping(value = ["/createPaymentOrder"], method = [RequestMethod.POST])
    fun createPaymentOrder(@RequestBody request: CreatePaymentOrderRequest): CreatePaymentOrderResponse? {
        return productOrderV3Service.createPaymentOrder(request)
    }

    @RequestMapping(value = ["/verifyAndCommitPayment"], method = [RequestMethod.POST])
    fun verifyAndCommitPayment(@RequestBody request: VerifyAndCommitPaymentRequest): VerifyAndCommitPaymentResponse? {
        return productOrderV3Service.verifyAndCommitPayment(request)
    }

}
