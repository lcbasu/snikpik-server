package com.server.shop.service

import com.server.shop.dto.*

abstract class ProductOrderV3Service {
    abstract fun updateCart(request: UpdateCartV3Request): SavedProductOrderV3Response?
    abstract fun updateStatus(request: ProductOrderStatusUpdateV3Request): SavedProductOrderV3Response?
    abstract fun getActiveProductOrderBag(): SavedProductOrderV3Response?
    abstract fun clearCart(): SavedProductOrderV3Response?
    abstract fun saveForLater(request: SaveForLaterRequest): SavedProductOrderV3Response?
    abstract fun updateDeliveryAddress(request: UpdateDeliveryAddressRequest): SavedProductOrderV3Response?
    abstract fun createPaymentOrder(request: CreatePaymentOrderRequest): CreatePaymentOrderResponse?
    abstract fun verifyAndCommitPayment(request: VerifyAndCommitPaymentRequest): VerifyAndCommitPaymentResponse?
    abstract fun getAllOrdersForLoggedInUser(): AllProductOrderV3Response?
}
