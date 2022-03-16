package com.server.shop.provider

import com.server.common.enums.ReadableIdPrefix
import com.server.common.provider.UniqueIdProvider
import com.server.common.utils.CommonUtils.convertToStringBlob
import com.server.common.utils.DateUtils
import com.server.shop.dao.ProductOrderStateChangeV3Repository
import com.server.shop.entities.ProductOrderStateChangeV3
import com.server.shop.entities.ProductOrderV3
import com.server.shop.enums.ProductOrderStatusV3
import com.server.shop.model.ProductOrderStateChangeDataV3
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ProductOrderStateChangeV3Provider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)
    @Autowired
    private lateinit var productOrderStateChangeV3Repository: ProductOrderStateChangeV3Repository

    @Autowired
    private lateinit var uniqueIdProvider: UniqueIdProvider

    @Autowired
    private lateinit var cartItemsV3Provider: CartItemsV3Provider

    fun getProductOrderStateChange(productOrderStateChangeId: String): ProductOrderStateChangeV3? =
        try {
            productOrderStateChangeV3Repository.findById(productOrderStateChangeId).get()
        } catch (e: Exception) {
            null
        }

    fun saveProductOrderStateChange(oldProductOrder: ProductOrderV3?, newProductOrder: ProductOrderV3): ProductOrderStateChangeV3 {
        val newProductOrderStateChange = ProductOrderStateChangeV3()
        newProductOrderStateChange.id = uniqueIdProvider.getUniqueIdAfterSaving(ReadableIdPrefix.OSC.name)
        newProductOrderStateChange.productOrder = newProductOrder
        newProductOrderStateChange.addedBy = newProductOrder.addedBy
        newProductOrderStateChange.stateChangeAt = DateUtils.dateTimeNow()
        newProductOrderStateChange.fromProductOrderStatus = oldProductOrder?.orderStatus ?: ProductOrderStatusV3.DRAFT
        newProductOrderStateChange.toProductOrderStatus = newProductOrder.orderStatus

        val productOrderStateChangeData = oldProductOrder?.let { ProductOrderStateChangeDataV3(
            addressId = oldProductOrder.deliveryAddress?.id,
            cartItems = cartItemsV3Provider.getCartItems(oldProductOrder).associateBy({it.id}, {it.totalUnits}),
            deliveryChargeInPaisa = oldProductOrder.deliveryChargeInPaisa,
            totalTaxInPaisa = oldProductOrder.totalTaxInPaisa,
            totalPriceWithoutTaxInPaisa = oldProductOrder.priceOfCartItemsWithoutTaxInPaisa,
            totalPricePayableInPaisa = oldProductOrder.totalPricePayableInPaisa,

            discountInPaisa = oldProductOrder.totalDiscountInPaisa,

            paymentMode = oldProductOrder.paymentMode,
            successPaymentId = oldProductOrder.successPayment?.id,
        ) } ?: ProductOrderStateChangeDataV3()
        newProductOrderStateChange.productOrderStateChangeData = convertToStringBlob(productOrderStateChangeData)
        return productOrderStateChangeV3Repository.save(newProductOrderStateChange)
    }

    fun getProductOrderStateChanges(productOrder: ProductOrderV3) =
        try {
            productOrderStateChangeV3Repository.findAllByProductOrder(productOrder)
        } catch (e: Exception) {
            emptyList()
        }
}
