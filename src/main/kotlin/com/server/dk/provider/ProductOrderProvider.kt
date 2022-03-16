package com.server.dk.provider

import com.server.common.enums.ReadableIdPrefix
import com.server.common.provider.UniqueIdProvider
import com.server.dk.dao.ProductOrderRepository
import com.server.dk.dto.*
import com.server.dk.entities.Company
import com.server.dk.entities.ProductOrder
import com.server.common.entities.User
import com.server.dk.enums.*
import com.server.dk.model.OrderStateTransitionOutput
import com.server.dk.model.ProductOrderStateBeforeUpdate
import com.server.dk.model.convertToString
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ProductOrderProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var productOrderRepository: ProductOrderRepository

    @Autowired
    private lateinit var uniqueIdProvider: UniqueIdProvider

    @Autowired
    private lateinit var addressProvider: AddressProvider

    @Autowired
    private lateinit var cartItemProvider: CartItemProvider

    @Autowired
    private lateinit var productVariantProvider: ProductVariantProvider

    @Autowired
    private lateinit var productProvider: ProductProvider

    @Autowired
    private lateinit var productCollectionProvider: ProductCollectionProvider

    @Autowired
    private lateinit var productOrderStateChangeProvider: ProductOrderStateChangeProvider

    @Autowired
    private lateinit var companyProvider: CompanyProvider

    @Autowired
    private lateinit var collectionProvider: CollectionProvider

    fun saveProductOrder(productOrder: ProductOrder): ProductOrder {
        val savedProductOrder = productOrderRepository.save(productOrder)
        productOrderStateChangeProvider.saveProductOrderStateChange(productOrder);
        return savedProductOrder
    }

    fun getProductOrder(productOrderId: String): ProductOrder? =
        try {
            productOrderRepository.findById(productOrderId).get()
        } catch (e: Exception) {
            null
        }

    fun getProductOrders(company: Company) =
        productOrderRepository.findAllByCompany(company)

    fun getProductOrders(company: Company, orderStatusNotIn: Set<ProductOrderStatus>) =
        productOrderRepository.findAllByCompanyAndOrderStatusNotIn(company, orderStatusNotIn)

    fun getProductOrders(user: User) =
        productOrderRepository.findAllByAddedBy(user)

    fun getProductOrders(company: Company, user: User) =
        productOrderRepository.findAllByCompanyAndAddedBy(company, user)

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
        // TODO: Update this to a bigger minLength once we start getting substantial number of orders
        newProductOrder.id = uniqueIdProvider.getUniqueIdAfterSaving(
            prefix = ReadableIdPrefix.ORD.name,
            onlyNumbers = true,
            minLength = 10,
            maxLength = 15)
        newProductOrder.addedBy = user
        newProductOrder.company = company
        newProductOrder.address = user.defaultAddressId?.let { addressProvider.getAddress(it) }
        return saveProductOrder(newProductOrder)
    }

    fun saveAndRefreshProductOrder(productOrder: ProductOrder): ProductOrder {
        productOrder.totalTaxInPaisa = 0
        productOrder.totalPriceWithoutTaxInPaisa = 0
        cartItemProvider.getCartItems(productOrder).map { cartItem ->
            productOrder.totalTaxInPaisa += cartItem.totalTaxInPaisa
            productOrder.totalPriceWithoutTaxInPaisa += cartItem.totalPriceWithoutTaxInPaisa
        }
        productOrder.totalPricePayableInPaisa = (productOrder.totalPriceWithoutTaxInPaisa + productOrder.totalTaxInPaisa + productOrder.deliveryChargeInPaisa) - productOrder.discountInPaisa
        return saveProductOrder(productOrder)
    }

    fun placeProductOrder(user: User, productOrderId: String): ProductOrder {
        val productOrder = getProductOrder(productOrderId) ?: error("No product order found for id: $productOrderId")
        val productOrderUser = productOrder.addedBy ?: error("Missing user for Product Order")

        if (productOrderUser.id != user.id) {
            error("Requesting user is not the same as the ordered user")
        }
        val updatedProductOrder = transitionStateTo(productOrder, ProductOrderStatus.PLACED)
        updateDependentModels(updatedProductOrder)
        return updatedProductOrder
    }

    fun getIsOrderTransitionPossible(productOrder: ProductOrder, newStatus: ProductOrderStatus): OrderStateTransitionOutput {
        if (productOrder.orderStatus == newStatus) error("Not required to move to $newStatus. Order is already in $newStatus state.")
        return when (newStatus) {
            ProductOrderStatus.PLACED -> {
                if (productOrder.orderStatus == ProductOrderStatus.PAYMENT_CONFIRMED) {
                    OrderStateTransitionOutput(
                        transitionPossible = true
                    )
                } else {
                    val errorMessage = "Can only move to $newStatus, from PAYMENT_CONFIRMED state and if the customer has confirmed the payment mode for COD and actually made the payment for ONLINE."
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
                if (productOrder.orderStatus == ProductOrderStatus.PLACED ||
                    productOrder.orderStatus == ProductOrderStatus.ACCEPTED_BY_SELLER ||
                    productOrder.orderStatus == ProductOrderStatus.ACCEPTED_BY_CUSTOMER ||

                    // if the earlier change was rejected by the customer or the seller
                    // and the seller has modified the order and sent to customer for approval again
                    productOrder.orderStatus == ProductOrderStatus.REJECTED_BY_CUSTOMER ||
                    productOrder.orderStatus == ProductOrderStatus.REJECTED_BY_SELLER) {
                    OrderStateTransitionOutput(
                        transitionPossible = true
                    )
                } else {
                    val errorMessage = "Can only move to $newStatus, from if seller has made some changes and the order has not been shipped."
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
                    productOrder.orderStatus == ProductOrderStatus.ACCEPTED_BY_CUSTOMER ||

                    // if the earlier change was rejected by the customer or the seller
                    // and the customer has modified the order and sent to seller for approval again
                    productOrder.orderStatus == ProductOrderStatus.REJECTED_BY_CUSTOMER ||
                    productOrder.orderStatus == ProductOrderStatus.REJECTED_BY_SELLER) {
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
                if (productOrder.orderStatus == ProductOrderStatus.PLACED ||
                    productOrder.orderStatus == ProductOrderStatus.ACCEPTED_BY_CUSTOMER ||
                    productOrder.orderStatus == ProductOrderStatus.PENDING_SELLER_APPROVAL) {
                    OrderStateTransitionOutput(
                        transitionPossible = true
                    )
                } else {
                    val errorMessage = "Can only move to $newStatus, if customer has placed the order or earlier modifications has been accepted by the customer or there is a pending approval to be approved by seller."
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
            ProductOrderStatus.ADDRESS_CONFIRMED -> if (productOrder.orderStatus == ProductOrderStatus.DRAFT) {
                OrderStateTransitionOutput(
                    transitionPossible = true
                )
            } else {
                val errorMessage = "Can only move to $newStatus, if the order is the new order (DRAFT) from this shop."
                logger.error(errorMessage)
                OrderStateTransitionOutput(
                    transitionPossible = false,
                    errorMessage = errorMessage
                )
            }
            ProductOrderStatus.PAYMENT_CONFIRMED -> if (productOrder.orderStatus == ProductOrderStatus.ADDRESS_CONFIRMED) {
                OrderStateTransitionOutput(
                    transitionPossible = true
                )
            } else {
                val errorMessage = "Can only move to $newStatus, if the order has a confirmed delivery address."
                logger.error(errorMessage)
                OrderStateTransitionOutput(
                    transitionPossible = false,
                    errorMessage = errorMessage
                )
            }
        }
    }

    fun transitionStateTo(productOrder: ProductOrder, newStatus: ProductOrderStatus): ProductOrder {
        val isOrderTransitionPossible = getIsOrderTransitionPossible(productOrder, newStatus)
        return if (isOrderTransitionPossible.transitionPossible) {
            // TODO: Send notification to Seller and Customer based on transition
            productOrder.orderStatus = newStatus
            saveProductOrder(productOrder)
        } else {
            error(isOrderTransitionPossible.errorMessage)
        }
    }

    private fun isCartUpdateValid(productOrder: ProductOrder, productOrderUpdateRequest: ProductOrderUpdateRequest): Boolean {
        val cartItems = cartItemProvider.getCartItems(productOrder).filterNot { it.totalUnits == 0L }
        val requestedCartItems = productOrderUpdateRequest.newCartUpdates.keys
        val presentCartItems = cartItems.map { it.id }.toSet()
        return presentCartItems.containsAll(requestedCartItems)
    }

    private fun isDeliveryChargeUpdateValid(productOrder: ProductOrder, productOrderUpdateRequest: ProductOrderUpdateBySellerRequest): Boolean {
        val reqDeliveryCharge = productOrderUpdateRequest.newDeliveryChargeInPaisa ?: return false
        return productOrder.deliveryChargeInPaisa != reqDeliveryCharge
    }

    private fun validateProductOrderUpdateRequest(productOrder: ProductOrder, productOrderUpdateRequest: ProductOrderUpdateRequest) {
        // Validation
        when (productOrderUpdateRequest.updatedBy) {
            ProductOrderUpdatedBy.BY_SELLER -> {
                productOrderUpdateRequest as ProductOrderUpdateBySellerRequest
                if (!isCartUpdateValid(productOrder, productOrderUpdateRequest) && !isDeliveryChargeUpdateValid(productOrder, productOrderUpdateRequest)) {
                    error("Either the cart quantity or the delivery charge has to be a part of the request")
                }
            }
            ProductOrderUpdatedBy.BY_CUSTOMER -> {
                productOrderUpdateRequest as ProductOrderUpdateByCustomerRequest
                if (productOrderUpdateRequest.newAddressId == null &&
                    productOrderUpdateRequest.paymentId == null &&
                    productOrderUpdateRequest.paymentMode == null &&
                    !isCartUpdateValid(productOrder, productOrderUpdateRequest)) {
                    error("Either the cart quantity or address or payment details has to be a part of the request")
                }
            }
        }
    }

    fun productOrderUpdate(user: User, productOrderUpdateRequest: ProductOrderUpdateRequest): ProductOrder {
        val productOrderId = productOrderUpdateRequest.productOrderId ?: error("Product id is required")
        val productOrder = getProductOrder(productOrderId) ?: error("No product order found for id: $productOrderId")

        validateProductOrderUpdateRequest(productOrder, productOrderUpdateRequest)

        val newStatus = when (productOrderUpdateRequest.updatedBy) {
            ProductOrderUpdatedBy.BY_SELLER -> ProductOrderStatus.PENDING_CUSTOMER_APPROVAL
            ProductOrderUpdatedBy.BY_CUSTOMER -> when (productOrder.orderStatus) {
                ProductOrderStatus.DRAFT -> {
                    ProductOrderStatus.ADDRESS_CONFIRMED
                }
                ProductOrderStatus.ADDRESS_CONFIRMED -> {
                    ProductOrderStatus.PAYMENT_CONFIRMED
                }
                else -> {
                    ProductOrderStatus.PENDING_SELLER_APPROVAL
                }
            }
        }

        val isOrderTransitionPossible = getIsOrderTransitionPossible(productOrder, newStatus)
        return if (isOrderTransitionPossible.transitionPossible) {
            val updatedProductOrder = if (newStatus == ProductOrderStatus.ADDRESS_CONFIRMED || newStatus == ProductOrderStatus.PAYMENT_CONFIRMED) {
                // No need to save old state
                productOrderUpdateByCustomer(productOrder, productOrderUpdateRequest)
            } else {
                saveOldStateAndUpdateProductOrder(productOrder, productOrderUpdateRequest)
            }
            transitionStateTo(updatedProductOrder, newStatus)
        } else {
            error(isOrderTransitionPossible.errorMessage)
        }
    }

    @Transactional
    fun saveOldStateAndUpdateProductOrder(productOrder: ProductOrder, productOrderUpdateRequest: ProductOrderUpdateRequest): ProductOrder {

        val productOrderId = productOrderUpdateRequest.productOrderId ?: error("Product id is required")
        if (productOrder.id != productOrderId) {
            error("Product order being updated and the product id to be updated are not same. productOrder.id: ${productOrder.id} & productOrderId: $productOrderId")
        }

        val productOrderCartItems = cartItemProvider.getCartItems(productOrder)

        val productOrderStateBeforeUpdate = ProductOrderStateBeforeUpdate(
            addressId = productOrder.address?.id,
            cartItems = productOrderCartItems.associateBy({it.id}, {it.totalUnits}),
            deliveryChargeInPaisa = productOrder.deliveryChargeInPaisa,
            totalTaxInPaisa = productOrder.totalTaxInPaisa,
            totalPriceWithoutTaxInPaisa = productOrder.totalPriceWithoutTaxInPaisa,
            totalPricePayableInPaisa = productOrder.totalPricePayableInPaisa,
        )

        productOrder.productOrderStateBeforeUpdate = productOrderStateBeforeUpdate.convertToString()

        when (productOrderUpdateRequest.updatedBy) {
            ProductOrderUpdatedBy.BY_SELLER -> {
                val productOrderUpdateBySellerRequest = productOrderUpdateRequest as ProductOrderUpdateBySellerRequest
                productOrderUpdateBySellerRequest.newDeliveryChargeInPaisa?.let {
                    if (productOrder.deliveryChargeInPaisa != it) {
                        productOrder.deliveryChargeInPaisa = it
                    }
                }
            }
            ProductOrderUpdatedBy.BY_CUSTOMER -> {
                productOrderUpdateByCustomer(productOrder, productOrderUpdateRequest)
            }
        }

        if (productOrderUpdateRequest.newCartUpdates.isNotEmpty()) {
            productOrderCartItems.map {
                val updatedCount = productOrderUpdateRequest.newCartUpdates.getOrDefault(it.id, -1L)
                if (updatedCount >= 0) {
                    cartItemProvider.updateProductInCart(it.id, updatedCount)
                }
            }
        }
        return saveAndRefreshProductOrder(productOrder)
    }

    private fun productOrderUpdateByCustomer(productOrder: ProductOrder, productOrderUpdateRequest: ProductOrderUpdateRequest): ProductOrder {
        productOrderAddressUpdateByCustomer(productOrder, productOrderUpdateRequest)

        if (productOrder.paymentMode == OrderPaymentMode.NONE) {
            productOrderPaymentUpdateByCustomer(productOrder, productOrderUpdateRequest)
        } else {
            val productOrderUpdateByCustomerRequest = productOrderUpdateRequest as ProductOrderUpdateByCustomerRequest
            if (productOrderUpdateByCustomerRequest.paymentMode != null) {
                error("You can not change payment method once order is placed")
            }
        }
        return saveAndRefreshProductOrder(productOrder)
    }

    private fun productOrderAddressUpdateByCustomer(productOrder: ProductOrder, productOrderUpdateRequest: ProductOrderUpdateRequest): ProductOrder {
        val productOrderUpdateByCustomerRequest = productOrderUpdateRequest as ProductOrderUpdateByCustomerRequest
        productOrderUpdateByCustomerRequest.newAddressId?.let {
            val productOrderAddress = productOrder.address
            if (productOrderAddress == null || productOrderAddress.id != it) {
                val address = addressProvider.getAddress(it) ?: error("Address does not exist for id: $it")
                val user = productOrder.addedBy
                    ?: error("User does not exist for product order with id: ${productOrder.id}")
                val isUserAddressValid = addressProvider.getIsUserAddressValid(user, address)
                if (!isUserAddressValid) {
                    error("Address doe not belong to the user.")
                }
                productOrder.address = address
            }
        }
        return saveAndRefreshProductOrder(productOrder)
    }

    // Allow this method to be called only ONCE
    private fun productOrderPaymentUpdateByCustomer(productOrder: ProductOrder, productOrderUpdateRequest: ProductOrderUpdateRequest): ProductOrder {
        val productOrderUpdateByCustomerRequest = productOrderUpdateRequest as ProductOrderUpdateByCustomerRequest
        productOrderUpdateByCustomerRequest.paymentMode?.let {
            when (it) {
                OrderPaymentMode.COD -> {
                    // Only payment mode changes
                    productOrder.paymentMode = it
                }
                OrderPaymentMode.ONLINE -> {
                    // Both payment mode and payment Id changes
                    val paymentId = productOrderUpdateByCustomerRequest.paymentId ?: error("Payment ID is required for online payments")
                    productOrder.paymentMode = it
                    productOrder.successPaymentId = paymentId
                }
                else -> {
                    error("Can only move to COD or ONLINE payment")
                }
            }
        }
        return saveAndRefreshProductOrder(productOrder)
    }

    fun productOrderUpdateApproval(user: User, productOrderStatusUpdateRequest: ProductOrderStatusUpdateRequest): ProductOrder {
        val productOrder = getProductOrder(productOrderStatusUpdateRequest.productOrderId) ?: error("No product order found for id: ${productOrderStatusUpdateRequest.productOrderId}")

        val newStatus = when (productOrderStatusUpdateRequest.updatedBy) {
            ProductOrderUpdatedBy.BY_SELLER -> {
                when (productOrderStatusUpdateRequest.updateType) {
                    ProductOrderUpdateType.ACCEPT -> ProductOrderStatus.ACCEPTED_BY_SELLER
                    ProductOrderUpdateType.REJECT -> ProductOrderStatus.REJECTED_BY_SELLER
                    ProductOrderUpdateType.CANCEL -> ProductOrderStatus.CANCELLED_BY_SELLER
                    ProductOrderUpdateType.SHIPPED -> ProductOrderStatus.SHIPPED_BY_SELLER
                    ProductOrderUpdateType.DELIVERED -> ProductOrderStatus.DELIVERED_BY_SELLER
                    ProductOrderUpdateType.FAILED -> ProductOrderStatus.FAILED_TO_DELIVER_BY_SELLER
                }
            }
            ProductOrderUpdatedBy.BY_CUSTOMER -> {
                when (productOrderStatusUpdateRequest.updateType) {
                    ProductOrderUpdateType.ACCEPT -> ProductOrderStatus.ACCEPTED_BY_CUSTOMER
                    ProductOrderUpdateType.REJECT -> ProductOrderStatus.REJECTED_BY_CUSTOMER
                    ProductOrderUpdateType.CANCEL -> ProductOrderStatus.CANCELLED_BY_CUSTOMER
                    ProductOrderUpdateType.SHIPPED -> error("Customer can not ship the order")
                    ProductOrderUpdateType.DELIVERED -> error("Customer can not deliver the order")
                    ProductOrderUpdateType.FAILED -> error("Customer can not mark the order failed")
                }
            }
        }

        // TODO: Add user role level checks on who can update what
        //val productOrderUser = productOrder.addedBy ?: error("Product order is missing user. productOrderId: ${productOrder.id}")

        val isOrderTransitionPossible = getIsOrderTransitionPossible(productOrder, newStatus)
        return if (isOrderTransitionPossible.transitionPossible) {
            // Remove the update as all the pending update has been approved
            productOrder.productOrderStateBeforeUpdate = ""
            productOrderStatusUpdateRequest.deliveryTimeId?.let {
                productOrder.deliveryTimeId = it
            }
            val updatedProductOrder = saveProductOrder(productOrder)
            transitionStateTo(updatedProductOrder, newStatus)
        } else {
            error(isOrderTransitionPossible.errorMessage)
        }
    }

    fun updateDependentModels(productOrder: ProductOrder) {
        companyProvider.updateOrderDetails(productOrder)
        productProvider.updateOrderDetails(productOrder)
        productVariantProvider.updateOrderDetails(productOrder)
        collectionProvider.updateOrderDetails(productOrder)
    }
}
