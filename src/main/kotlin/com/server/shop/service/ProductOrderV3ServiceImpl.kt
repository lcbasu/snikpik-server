package com.server.shop.service

import com.server.shop.dto.*
import com.server.shop.provider.ProductOrderV3Provider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ProductOrderV3ServiceImpl : ProductOrderV3Service() {

    @Autowired
    private lateinit var productOrderV3Provider: ProductOrderV3Provider

    override fun updateCart(request: UpdateCartV3Request): SavedProductOrderV3Response? {
        return productOrderV3Provider.updateCart(request)?.updatedProductOrder?.toSavedProductOrderV3Response()
    }

    override fun updateStatus(request: ProductOrderStatusUpdateV3Request): SavedProductOrderV3Response? {
        return productOrderV3Provider.updateStatus(request)?.toSavedProductOrderV3Response()
    }

    override fun getActiveProductOrderBag(): SavedProductOrderV3Response? {
        return productOrderV3Provider.getActiveProductOrderBag()?.toSavedProductOrderV3Response()
    }

    override fun clearCart(): SavedProductOrderV3Response? {
        return productOrderV3Provider.clearCart()?.toSavedProductOrderV3Response()
    }

    override fun saveForLater(request: SaveForLaterRequest): SavedProductOrderV3Response? {
        return productOrderV3Provider.saveForLater(request)?.updatedProductOrder?.toSavedProductOrderV3Response()
    }

    override fun updateDeliveryAddress(request: UpdateDeliveryAddressRequest): SavedProductOrderV3Response? {
        return productOrderV3Provider.updateDeliveryAddress(request)?.toSavedProductOrderV3Response()
    }

    override fun createPaymentOrder(request: CreatePaymentOrderRequest): CreatePaymentOrderResponse? {
        return productOrderV3Provider.createPaymentOrder(request)
    }

    override fun verifyAndCommitPayment(request: VerifyAndCommitPaymentRequest): VerifyAndCommitPaymentResponse? {
        return productOrderV3Provider.verifyAndCommitPayment(request)
    }

    override fun getAllOrdersForLoggedInUser(): AllProductOrderV3Response? {
        return AllProductOrderV3Response(productOrderV3Provider.getAllOrdersForLoggedInUser().map { it.toSavedProductOrderV3Response() })
    }
}
