package com.dukaankhata.server.utils

import com.dukaankhata.server.dao.ProductOrderRepository
import com.dukaankhata.server.entities.CartItem
import com.dukaankhata.server.entities.Company
import com.dukaankhata.server.entities.ProductOrder
import com.dukaankhata.server.entities.User
import com.dukaankhata.server.enums.ProductOrderStatus
import com.dukaankhata.server.enums.ReadableIdPrefix
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ProductOrderUtils {

    @Autowired
    private lateinit var productOrderRepository: ProductOrderRepository

    @Autowired
    private lateinit var uniqueIdGeneratorUtils: UniqueIdGeneratorUtils

    @Autowired
    private lateinit var addressUtils: AddressUtils

    fun getProductOrder(productOrderId: String): ProductOrder? =
        try {
            productOrderRepository.findById(productOrderId).get()
        } catch (e: Exception) {
            null
        }

    fun getProductOrders(company: Company, user: User, productOrderStatus: ProductOrderStatus) =
        productOrderRepository.findAllByCompanyAndAddedByAndOrderStatus(company, user, productOrderStatus)

    fun getProductOrders(user: User, productOrderStatus: ProductOrderStatus) =
        productOrderRepository.findAllByAddedByAndOrderStatus(user, productOrderStatus)

    fun getActiveProductOrderBag(user: User): List<ProductOrder> {
        val draftOrders = getProductOrders(user, ProductOrderStatus.DRAFT)

        val companies = draftOrders.filterNot { it.company == null }.groupBy { it.company }

        if (companies.size != draftOrders.size) {
            error("User has incorrect active bags for one or more companies")
        }
        return draftOrders
    }

    fun getActiveProductOrderBag(company: Company, user: User): ProductOrder? {
        val draftOrders = getProductOrders(company, user, ProductOrderStatus.DRAFT)

        if (draftOrders.size > 1) {
            error("There should be only one Cart active for a customer and a company for ")
        }

        if (draftOrders.size == 1) {
            return draftOrders.first()
        }
        return null
    }

    fun getOrCreateActiveProductOrderBag(company: Company, user: User): ProductOrder {
        return getActiveProductOrderBag(company = company, user = user) ?:
        createProductOrder(company, user, ProductOrderStatus.DRAFT)
    }

    fun createProductOrder(company: Company, user: User, productOrderStatus: ProductOrderStatus): ProductOrder {
        val newProductOrder = ProductOrder()
        newProductOrder.id = uniqueIdGeneratorUtils.getUniqueId(ReadableIdPrefix.ORD.name)
        newProductOrder.addedBy = user
        newProductOrder.company = company
        newProductOrder.address = user.defaultAddressId?.let { addressUtils.getAddress(it) }
        return productOrderRepository.save(newProductOrder)
    }

    fun updateProductOrder(productOrder: ProductOrder, cartItems: List<CartItem>): ProductOrder {
        productOrder.totalTaxInPaisa = 0
        productOrder.totalPriceWithoutTaxInPaisa = 0

        cartItems.map { cartItem ->
            productOrder.totalTaxInPaisa += cartItem.totalTaxInPaisa
            productOrder.totalPriceWithoutTaxInPaisa += cartItem.totalPriceWithoutTaxInPaisa
        }

        productOrder.totalPricePayableInPaisa = (productOrder.totalPriceWithoutTaxInPaisa + productOrder.totalTaxInPaisa + productOrder.deliveryChargeInPaisa) - productOrder.discountInPaisa
        return productOrderRepository.save(productOrder)
    }
}
