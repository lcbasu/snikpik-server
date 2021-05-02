package com.dukaankhata.server.utils

import com.dukaankhata.server.dao.ProductOrderRepository
import com.dukaankhata.server.dto.ProductOrderUpdateByCustomerRequest
import com.dukaankhata.server.dto.ProductOrderUpdateBySellerRequest
import com.dukaankhata.server.dto.ProductOrderUpdateRequest
import com.dukaankhata.server.entities.CartItem
import com.dukaankhata.server.entities.Company
import com.dukaankhata.server.entities.ProductOrder
import com.dukaankhata.server.entities.User
import com.dukaankhata.server.enums.ProductOrderApprovalBy
import com.dukaankhata.server.enums.ProductOrderStatus
import com.dukaankhata.server.enums.ProductOrderUpdateType
import com.dukaankhata.server.enums.ReadableIdPrefix
import com.dukaankhata.server.model.OrderStateTransitionOutput
import com.dukaankhata.server.model.ProductOrderUpdate
import com.dukaankhata.server.model.convertToString
import com.dukaankhata.server.model.getProductOrderUpdate
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ProductOrderUtils {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var productOrderRepository: ProductOrderRepository

    @Autowired
    private lateinit var uniqueIdGeneratorUtils: UniqueIdGeneratorUtils

    @Autowired
    private lateinit var addressUtils: AddressUtils

    @Autowired
    private lateinit var cartItemUtils: CartItemUtils

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

    fun updateProductOrderAddress(user: User, newAddressId: String, productOrderId: String): ProductOrder {
        val address = addressUtils.getAddress(newAddressId) ?: error("Address id: $newAddressId does not have any address")
        val isUserAddressValid = addressUtils.getIsUserAddressValid(user, address)
        if (!isUserAddressValid) {
            error("Address doe not belong to the user.")
        }
        val productOrder = getProductOrder(productOrderId) ?: error("No product order found for id: $productOrderId")
        productOrder.address = address
        return productOrderRepository.save(productOrder)
    }

    fun placeProductOrder(user: User, productOrderId: String): ProductOrder {
        val productOrder = getProductOrder(productOrderId) ?: error("No product order found for id: $productOrderId")
        val productOrderUser = productOrder.addedBy ?: error("Missing user for Product Order")

        if (productOrderUser.id != user.id) {
            error("Requesting user is not the same as the ordered user")
        }
        return transitionStateTo(productOrder, ProductOrderStatus.PLACED)
    }

    fun getIsOrderTransitionPossible(productOrder: ProductOrder, newStatus: ProductOrderStatus): OrderStateTransitionOutput {
        return when (newStatus) {
            ProductOrderStatus.PLACED -> {
                if (productOrder.orderStatus == ProductOrderStatus.DRAFT) {
                    OrderStateTransitionOutput(
                        transitionPossible = true
                    )
                } else {
                    val errorMessage = "Can only move to $newStatus, from DRAFT state and if the customer has decided to place the order."
                    logger.error(errorMessage)
                    OrderStateTransitionOutput(
                        transitionPossible = false,
                        errorMessage = errorMessage
                    )
                }
            }
            ProductOrderStatus.DRAFT -> {
                val errorMessage = "Can not move the order to DRAFT state in any scenario"
                logger.error(errorMessage)
                OrderStateTransitionOutput(
                    transitionPossible = false,
                    errorMessage = errorMessage
                )
            }
            ProductOrderStatus.PENDING_CUSTOMER_APPROVAL -> {
                if (productOrder.orderStatus == ProductOrderStatus.PLACED) {
                    OrderStateTransitionOutput(
                        transitionPossible = true
                    )
                } else {
                    val errorMessage = "Can only move to $newStatus, from PLACED state and if some changes have been made by the seller"
                    logger.error(errorMessage)
                    OrderStateTransitionOutput(
                        transitionPossible = false,
                        errorMessage = errorMessage
                    )
                }
            }
            ProductOrderStatus.ACCEPTED_BY_CUSTOMER -> {
                if (productOrder.orderStatus == ProductOrderStatus.PENDING_CUSTOMER_APPROVAL) {
                    OrderStateTransitionOutput(
                        transitionPossible = true
                    )
                } else {
                    val errorMessage = "Can only move to $newStatus, if customer has accepted the modifications."
                    logger.error(errorMessage)
                    OrderStateTransitionOutput(
                        transitionPossible = false,
                        errorMessage = errorMessage
                    )
                }
            }
            ProductOrderStatus.REJECTED_BY_CUSTOMER -> {
                if (productOrder.orderStatus == ProductOrderStatus.PENDING_CUSTOMER_APPROVAL) {
                    OrderStateTransitionOutput(
                        transitionPossible = true
                    )
                } else {
                    val errorMessage = "Can only move to $newStatus, if customer has accepted the modifications."
                    logger.error(errorMessage)
                    OrderStateTransitionOutput(
                        transitionPossible = false,
                        errorMessage = errorMessage
                    )
                }
            }
            ProductOrderStatus.CANCELLED_BY_CUSTOMER -> {
                if (productOrder.orderStatus == ProductOrderStatus.PLACED || productOrder.orderStatus == ProductOrderStatus.ACCEPTED_BY_SELLER) {
                    OrderStateTransitionOutput(
                        transitionPossible = true
                    )
                } else {
                    val errorMessage = "Can only move to $newStatus, if customer has placed the order and it is not yet accepted by the seller or not yet shipped."
                    logger.error(errorMessage)
                    OrderStateTransitionOutput(
                        transitionPossible = false,
                        errorMessage = errorMessage
                    )
                }
            }
            ProductOrderStatus.PENDING_SELLER_APPROVAL -> {
                if (productOrder.orderStatus == ProductOrderStatus.PLACED ||
                    productOrder.orderStatus == ProductOrderStatus.ACCEPTED_BY_SELLER ||
                    productOrder.orderStatus == ProductOrderStatus.ACCEPTED_BY_CUSTOMER) {
                    OrderStateTransitionOutput(
                        transitionPossible = true
                    )
                } else {
                    val errorMessage = "Can only move to $newStatus, if customer decided to update the order details before it has been shipped."
                    logger.error(errorMessage)
                    OrderStateTransitionOutput(
                        transitionPossible = false,
                        errorMessage = errorMessage
                    )
                }
            }
            ProductOrderStatus.ACCEPTED_BY_SELLER -> {
                if (productOrder.orderStatus == ProductOrderStatus.PLACED ||
                    productOrder.orderStatus == ProductOrderStatus.ACCEPTED_BY_CUSTOMER ||
                    productOrder.orderStatus == ProductOrderStatus.PENDING_SELLER_APPROVAL) {
                    OrderStateTransitionOutput(
                        transitionPossible = true
                    )
                } else {
                    val errorMessage = "Can only move to $newStatus, if customer has placed the order or customer has accepted the changes made by the seller."
                    logger.error(errorMessage)
                    OrderStateTransitionOutput(
                        transitionPossible = false,
                        errorMessage = errorMessage
                    )
                }
            }
            ProductOrderStatus.SHIPPED_BY_SELLER -> {
                if (productOrder.orderStatus == ProductOrderStatus.ACCEPTED_BY_SELLER) {
                    OrderStateTransitionOutput(
                        transitionPossible = true
                    )
                } else {
                    val errorMessage = "Can only move to $newStatus, if order has been accepted by the seller."
                    logger.error(errorMessage)
                    OrderStateTransitionOutput(
                        transitionPossible = false,
                        errorMessage = errorMessage
                    )
                }
            }
            ProductOrderStatus.DELIVERED_BY_SELLER -> {
                if (productOrder.orderStatus == ProductOrderStatus.SHIPPED_BY_SELLER) {
                    OrderStateTransitionOutput(
                        transitionPossible = true
                    )
                } else {
                    val errorMessage = "Can only move to $newStatus, if order has been shipped by the seller."
                    logger.error(errorMessage)
                    OrderStateTransitionOutput(
                        transitionPossible = false,
                        errorMessage = errorMessage
                    )
                }
            }
            ProductOrderStatus.FAILED_TO_DELIVER_BY_SELLER -> {
                if (productOrder.orderStatus == ProductOrderStatus.SHIPPED_BY_SELLER) {
                    OrderStateTransitionOutput(
                        transitionPossible = true
                    )
                } else {
                    val errorMessage = "Can only move to $newStatus, if order has been shipped by the seller."
                    logger.error(errorMessage)
                    OrderStateTransitionOutput(
                        transitionPossible = false,
                        errorMessage = errorMessage
                    )
                }
            }
            ProductOrderStatus.REJECTED_BY_SELLER -> {
                if (productOrder.orderStatus == ProductOrderStatus.PLACED || productOrder.orderStatus == ProductOrderStatus.ACCEPTED_BY_CUSTOMER) {
                    OrderStateTransitionOutput(
                        transitionPossible = true
                    )
                } else {
                    val errorMessage = "Can only move to $newStatus, if customer has placed the order or has accepted the earlier modification made by the seller."
                    logger.error(errorMessage)
                    OrderStateTransitionOutput(
                        transitionPossible = false,
                        errorMessage = errorMessage
                    )
                }
            }
            ProductOrderStatus.CANCELLED_BY_SELLER -> TODO()
            ProductOrderStatus.RETURNED_TO_OWNER -> {
                if (productOrder.orderStatus == ProductOrderStatus.SHIPPED_BY_SELLER) {
                    OrderStateTransitionOutput(
                        transitionPossible = true
                    )
                } else {
                    val errorMessage = "Can only move to $newStatus, if customer refuses to accept the product that is already shipped."
                    logger.error(errorMessage)
                    OrderStateTransitionOutput(
                        transitionPossible = false,
                        errorMessage = errorMessage
                    )
                }
            }
        }
    }

    fun transitionStateTo(productOrder: ProductOrder, newStatus: ProductOrderStatus): ProductOrder {
        val isOrderTransitionPossible = getIsOrderTransitionPossible(productOrder, newStatus)
        return if (isOrderTransitionPossible.transitionPossible) {
            // TODO: Send notification to Seller and Customer based on transition
            productOrder.orderStatus = newStatus
            productOrderRepository.save(productOrder)
        } else {
            error(isOrderTransitionPossible.errorMessage)
        }
    }

    fun productOrderUpdateByCustomer(user: User, productOrderUpdateByCustomerRequest: ProductOrderUpdateByCustomerRequest): ProductOrder {
        return sendProductOrderUpdateForApproval(user, productOrderUpdateByCustomerRequest, ProductOrderStatus.PENDING_SELLER_APPROVAL)
    }

    fun productOrderUpdateBySeller(user: User, productOrderUpdateBySellerRequest: ProductOrderUpdateBySellerRequest): ProductOrder {
        return sendProductOrderUpdateForApproval(user, productOrderUpdateBySellerRequest, ProductOrderStatus.PENDING_CUSTOMER_APPROVAL)
    }

    fun sendProductOrderUpdateForApproval(user: User, productOrderUpdateRequest: ProductOrderUpdateRequest, newStatus: ProductOrderStatus): ProductOrder {
        val productOrderId = productOrderUpdateRequest.productOrderId ?: error("Product id is required")
        val productOrder = getProductOrder(productOrderId) ?: error("No product order found for id: $productOrderId")
        val isOrderTransitionPossible = getIsOrderTransitionPossible(productOrder, newStatus)
        return if (isOrderTransitionPossible.transitionPossible) {
            val productOrderUpdate = getProductOrderUpdateForApproval(productOrder, productOrderUpdateRequest)
            val productOrderUpdateStr = productOrderUpdate.convertToString()
            if (productOrderUpdateStr.isBlank()) {
                error("No delta update was created for the updated order with orderId: $productOrderId")
            }
            productOrder.productOrderUpdate = productOrderUpdateStr
            productOrderRepository.save(productOrder)
            // Nothing except productOrderUpdate field in ProductOrder entity should change
            // unless approved by the Seller
            transitionStateTo(productOrder, newStatus)
        } else {
            error(isOrderTransitionPossible.errorMessage)
        }
    }

    fun getProductOrderUpdateForApproval(productOrder: ProductOrder, productOrderUpdateRequest: ProductOrderUpdateRequest): ProductOrderUpdate {

        var toBeUpdatedToTotalTaxInPaisa: Long? = null
        var toBeUpdatedToTotalPriceWithoutTaxInPaisa: Long? = null
        var toBeUpdatedToTotalPricePayableInPaisa: Long? = null

        var toBeUpdatedToDeliveryChargeInPaisa: Long? = null
        var toBeUpdatedToAddressId: String? = null
        val toBeUpdatedToCartUpdates = productOrderUpdateRequest.newCartUpdates

        val productOrderId = productOrderUpdateRequest.productOrderId ?: error("Product id is required")
        if (productOrder.id != productOrderId) {
            error("Product order being updated and requested product id to be updated are not same")
        }

        when (productOrderUpdateRequest.type) {
            ProductOrderUpdateType.BY_SELLER -> {
                val productOrderUpdateBySellerRequest = productOrderUpdateRequest as ProductOrderUpdateBySellerRequest
                productOrderUpdateBySellerRequest.newDeliveryChargeInPaisa?.let {
                    if (productOrder.deliveryChargeInPaisa != it) {
                        toBeUpdatedToDeliveryChargeInPaisa = it
                    }
                }
            }
            ProductOrderUpdateType.BY_CUSTOMER -> {
                val productOrderUpdateByCustomerRequest = productOrderUpdateRequest as ProductOrderUpdateByCustomerRequest
                productOrderUpdateByCustomerRequest.newAddressId?.let {
                    if (productOrder.address?.id != it) {
                        addressUtils.getAddress(it) ?: error("Address does not exist for id: $it")
                        toBeUpdatedToAddressId = it
                    }
                }
            }
        }

        val cartAfterUpdateWillLookLike = mutableMapOf<String, Long>()
        if (toBeUpdatedToCartUpdates.isNotEmpty()) {
            val currentCartItems = cartItemUtils.getCartItems(productOrder)
            currentCartItems.map {
                val updatedCount = toBeUpdatedToCartUpdates.getOrDefault(it.id, -1L)
                if (updatedCount >= 0) {
                    cartAfterUpdateWillLookLike[it.id] = updatedCount
                } else {
                    cartAfterUpdateWillLookLike[it.id] = it.totalUnits
                }
            }
        }

        if (cartAfterUpdateWillLookLike.isNotEmpty()) {
            val newDeliveryCharge = toBeUpdatedToDeliveryChargeInPaisa ?: productOrder.deliveryChargeInPaisa
            var totalTaxInPaisa = 0L
            var totalPriceWithoutTaxInPaisa = 0L

            cartAfterUpdateWillLookLike.map { cartItemEntry ->
                val cartItem = cartItemUtils.getCartItem(cartItemEntry.key) ?: error("Unable to get cart item for id: ${cartItemEntry.key}")
                val product = cartItem.product ?: error("Cart Items should always have product. Cart id: ${cartItem.id}")
                val totalUnits = cartItemEntry.value
                if (totalUnits <= 0L) {
                    totalTaxInPaisa += 0
                    totalPriceWithoutTaxInPaisa += 0
                } else {
                    totalTaxInPaisa += product.taxPerUnitInPaisa * totalUnits
                    totalPriceWithoutTaxInPaisa += product.pricePerUnitInPaisa * totalUnits
                }
            }
            // TODO: Check if the discount is applicable for the new rder value
            // A discount might have the clause that if the order value is below a certain
            // amount then remove the discount
            toBeUpdatedToTotalTaxInPaisa = totalTaxInPaisa
            toBeUpdatedToTotalPriceWithoutTaxInPaisa = totalPriceWithoutTaxInPaisa
            toBeUpdatedToTotalPricePayableInPaisa = (totalPriceWithoutTaxInPaisa + totalTaxInPaisa + newDeliveryCharge) - productOrder.discountInPaisa
        } else if (toBeUpdatedToDeliveryChargeInPaisa != null) {
            // Only the delivery amount is updated and not the cart items
            val newDeliveryCharge = toBeUpdatedToDeliveryChargeInPaisa ?: error("Delivery charge has to be present in this case")
            toBeUpdatedToTotalPricePayableInPaisa = (productOrder.totalPriceWithoutTaxInPaisa + productOrder.totalTaxInPaisa + newDeliveryCharge) - productOrder.discountInPaisa
        }
        val productOrderUpdate = ProductOrderUpdate(
            newTotalTaxInPaisa = toBeUpdatedToTotalTaxInPaisa,
            newTotalPriceWithoutTaxInPaisa = toBeUpdatedToTotalPriceWithoutTaxInPaisa,
            newTotalPricePayableInPaisa = toBeUpdatedToTotalPricePayableInPaisa,
            newDeliveryChargeInPaisa = toBeUpdatedToDeliveryChargeInPaisa,
            newAddressId = toBeUpdatedToAddressId,
            newCartUpdates = toBeUpdatedToCartUpdates,
        )

        when (productOrderUpdateRequest.type) {
            ProductOrderUpdateType.BY_SELLER -> {
                // newCartUpdates or newDeliveryChargeInPaisa MUST have some value otherwise the update failed
                if (productOrderUpdate.newCartUpdates.isEmpty() && productOrderUpdate.newDeliveryChargeInPaisa == null) {
                    error("Product update for seller failed")
                }
            }
            ProductOrderUpdateType.BY_CUSTOMER -> {
                // newCartUpdates or newAddressId MUST have some value otherwise the update failed
                if (productOrderUpdate.newCartUpdates.isEmpty() && productOrderUpdate.newAddressId == null) {
                    error("Product update for customer failed")
                }
            }
        }

        return productOrderUpdate
    }

    fun approveProductOrderUpdateByCustomer(user: User, productOrderId: String): ProductOrder {
        return applyProductOrderUpdate(user, productOrderId, ProductOrderApprovalBy.BY_CUSTOMER)
    }

    fun approveProductOrderUpdateBySeller(user: User, productOrderId: String): ProductOrder {
        return applyProductOrderUpdate(user, productOrderId, ProductOrderApprovalBy.BY_SELLER)
    }

    fun applyProductOrderUpdate(user: User, productOrderId: String, type: ProductOrderApprovalBy): ProductOrder {
        val productOrder = getProductOrder(productOrderId) ?: error("No product order found for id: $productOrderId")

        val newStatus = when (type) {
            ProductOrderApprovalBy.BY_SELLER -> {
                ProductOrderStatus.ACCEPTED_BY_SELLER
            }
            ProductOrderApprovalBy.BY_CUSTOMER -> {
                ProductOrderStatus.ACCEPTED_BY_CUSTOMER
            }
        }

        val productOrderUpdate = productOrder.getProductOrderUpdate()

        if (newStatus == ProductOrderStatus.ACCEPTED_BY_SELLER) {
            // newCartUpdates or newAddressId MUST have some value which was updated by the customer
            // and needs to be Approved by the Seller
            if (productOrderUpdate.newCartUpdates.isEmpty() && productOrderUpdate.newAddressId == null) {
                error("Product update for customer failed")
            }
        } else if (newStatus == ProductOrderStatus.ACCEPTED_BY_SELLER) {
            // newCartUpdates or newDeliveryChargeInPaisa MUST have some value
            // which was updated by the seller and should be Approved by the customer
            if (productOrderUpdate.newCartUpdates.isEmpty() && productOrderUpdate.newDeliveryChargeInPaisa == null) {
                error("Product update for seller failed")
            }
        } else {
            error("Incorrect productOrderUpdate for productOrderId: $productOrderId. Can not be approved.")
        }

        val isOrderTransitionPossible = getIsOrderTransitionPossible(productOrder, newStatus)
        return if (isOrderTransitionPossible.transitionPossible) {
            val updatedProductOrder = applyProductOrderUpdate(productOrder, productOrderUpdate)
            transitionStateTo(updatedProductOrder, newStatus)
        } else {
            error(isOrderTransitionPossible.errorMessage)
        }
    }

    private fun applyProductOrderUpdate(productOrder: ProductOrder, productOrderUpdate: ProductOrderUpdate): ProductOrder {

        productOrderUpdate.newTotalTaxInPaisa?.let {
            productOrder.totalTaxInPaisa = it
        }

        productOrderUpdate.newTotalPriceWithoutTaxInPaisa?.let {
            productOrder.totalPriceWithoutTaxInPaisa = it
        }

        productOrderUpdate.newAddressId?.let {
            val address = addressUtils.getAddress(it) ?: error("Address does not exist for id: $it")
            productOrder.address = address
        }

        productOrderUpdate.newDeliveryChargeInPaisa?.let {
            productOrder.deliveryChargeInPaisa = it
        }

        productOrderUpdate.newTotalPricePayableInPaisa?.let {
            productOrder.totalPricePayableInPaisa = it
        }

        if (productOrderUpdate.newCartUpdates.isNotEmpty()) {
            productOrderUpdate.newCartUpdates.map {
                val cartItemId = it.key
                val newCount = it.value
                cartItemUtils.updateProductInCart(cartItemId, newCount)
            }
        }
        return productOrderRepository.save(productOrder)
    }
}
