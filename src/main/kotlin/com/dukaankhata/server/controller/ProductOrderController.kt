package com.dukaankhata.server.controller

import com.dukaankhata.server.dto.*
import com.dukaankhata.server.service.ProductOrderService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

// ONLY Authenticated with Phone number APIs
@RestController
@RequestMapping("productOrder")
class ProductOrderController {

    @Autowired
    private lateinit var productOrderService: ProductOrderService

    @RequestMapping(value = ["/placeProductOrder"], method = [RequestMethod.POST])
    fun placeProductOrder(@RequestBody placeProductOrderRequest: PlaceProductOrderRequest): SavedProductOrderResponse {
        return productOrderService.placeProductOrder(placeProductOrderRequest)
    }

    @RequestMapping(value = ["/productOrderUpdateByCustomer"], method = [RequestMethod.POST])
    fun productOrderUpdateByCustomer(@RequestBody productOrderUpdateByCustomerRequest: ProductOrderUpdateByCustomerRequest): SavedProductOrderResponse {
        return productOrderService.productOrderUpdate(productOrderUpdateByCustomerRequest)
    }

    @RequestMapping(value = ["/productOrderUpdateBySeller"], method = [RequestMethod.POST])
    fun productOrderUpdateBySeller(@RequestBody productOrderUpdateBySellerRequest: ProductOrderUpdateBySellerRequest): SavedProductOrderResponse {
        return productOrderService.productOrderUpdate(productOrderUpdateBySellerRequest)
    }

    @RequestMapping(value = ["/productOrderUpdateStatusUpdate"], method = [RequestMethod.POST])
    fun productOrderUpdateStatusUpdate(@RequestBody productOrderStatusUpdateRequest: ProductOrderStatusUpdateRequest): SavedProductOrderResponse {
        return productOrderService.productOrderUpdateStatusUpdate(productOrderStatusUpdateRequest)
    }

    @RequestMapping(value = ["/getAll/{companyId}"], method = [RequestMethod.GET])
    fun getAllProductOrders(@PathVariable companyId: String): AllProductOrdersResponse {
        return productOrderService.getAllProductOrders(companyId)
    }

    @RequestMapping(value = ["/getAllProductOrderCards/{companyId}"], method = [RequestMethod.GET])
    fun getAllProductOrderCards(@PathVariable companyId: String): AllProductOrderCardsResponse {
        return productOrderService.getAllProductOrderCards(companyId)
    }
}
