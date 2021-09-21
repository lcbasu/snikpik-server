package com.dukaankhata.server.provider

import com.dukaankhata.server.dao.ProductOrderStateChangeRepository
import com.dukaankhata.server.entities.ProductOrder
import com.dukaankhata.server.entities.ProductOrderStateChange
import com.dukaankhata.server.enums.ReadableIdPrefix
import com.dukaankhata.server.model.ProductOrderStateChangeData
import com.dukaankhata.server.model.convertToString
import com.dukaankhata.server.utils.DateUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ProductOrderStateChangeProvider {

    @Autowired
    private lateinit var productOrderStateChangeRepository: ProductOrderStateChangeRepository

    @Autowired
    private lateinit var uniqueIdProvider: UniqueIdProvider

    @Autowired
    private lateinit var cartItemProvider: CartItemProvider

    fun getProductOrderStateChange(productOrderStateChangeId: String): ProductOrderStateChange? =
        try {
            productOrderStateChangeRepository.findById(productOrderStateChangeId).get()
        } catch (e: Exception) {
            null
        }

    fun saveProductOrderStateChange(productOrder: ProductOrder): ProductOrderStateChange {
        val newProductOrderStateChange = ProductOrderStateChange()
        newProductOrderStateChange.id = uniqueIdProvider.getUniqueId(ReadableIdPrefix.OSC.name)
        newProductOrderStateChange.productOrder = productOrder
        newProductOrderStateChange.addedBy = productOrder.addedBy
        newProductOrderStateChange.company = productOrder.company
        newProductOrderStateChange.stateChangeAt = DateUtils.dateTimeNow()
        newProductOrderStateChange.productOrderStatus = productOrder.orderStatus

        val productOrderCartItems = cartItemProvider.getCartItems(productOrder)
        val productOrderStateChangeData = ProductOrderStateChangeData(
            addressId = productOrder.address?.id,
            cartItems = productOrderCartItems.associateBy({it.id}, {it.totalUnits}),
            deliveryChargeInPaisa = productOrder.deliveryChargeInPaisa,
            totalTaxInPaisa = productOrder.totalTaxInPaisa,
            totalPriceWithoutTaxInPaisa = productOrder.totalPriceWithoutTaxInPaisa,
            totalPricePayableInPaisa = productOrder.totalPricePayableInPaisa,

            discountInPaisa = productOrder.discountInPaisa,
            discountId = productOrder.discount?.id,

            productOrderStateBeforeUpdate = productOrder.productOrderStateBeforeUpdate,

            paymentMode = productOrder.paymentMode,
            successPaymentId = productOrder.successPaymentId,
        )
        newProductOrderStateChange.productOrderStateChangeData = productOrderStateChangeData.convertToString()
        return productOrderStateChangeRepository.save(newProductOrderStateChange)
    }

    fun getProductOrderStateChanges(productOrder: ProductOrder) =
        try {
            productOrderStateChangeRepository.findAllByProductOrder(productOrder)
        } catch (e: Exception) {
            emptyList()
        }
}
