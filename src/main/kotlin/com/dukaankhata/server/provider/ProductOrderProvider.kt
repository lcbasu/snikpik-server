package com.dukaankhata.server.provider

import com.dukaankhata.server.dao.ProductOrderRepository
import com.dukaankhata.server.dto.*
import com.dukaankhata.server.entities.Company
import com.dukaankhata.server.entities.ProductOrder
import com.dukaankhata.server.entities.User
import com.dukaankhata.server.enums.*
import com.dukaankhata.server.model.OrderStateTransitionOutput
import com.dukaankhata.server.model.ProductOrderStateBeforeUpdate
import com.dukaankhata.server.model.convertToString
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

    fun getProductOrder(productOrderId: String): ProductOrder? =
        try {
            productOrderRepository.findById(productOrderId).get()
        } catch (e: Exception) {
            null
        }

    fun getProductOrders(company: Company) =
        productOrderRepository.findAllByCompany(company)

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
        newProductOrder.id = uniqueIdProvider.getUniqueId(ReadableIdPrefix.ORD.name)
        newProductOrder.addedBy = user
        newProductOrder.company = company
        newProductOrder.address = user.defaultAddressId?.let { addressProvider.getAddress(it) }
        return productOrderRepository.save(newProductOrder)
    }

    fun refreshProductOrder(productOrder: ProductOrder): ProductOrder {
        productOrder.totalTaxInPaisa = 0
        productOrder.totalPriceWithoutTaxInPaisa = 0
        cartItemProvider.getCartItems(productOrder).map { cartItem ->
            productOrder.totalTaxInPaisa += cartItem.totalTaxInPaisa
            productOrder.totalPriceWithoutTaxInPaisa += cartItem.totalPriceWithoutTaxInPaisa
        }
        productOrder.totalPricePayableInPaisa = (productOrder.totalPriceWithoutTaxInPaisa + productOrder.totalTaxInPaisa + productOrder.deliveryChargeInPaisa) - productOrder.discountInPaisa
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

    fun productOrderUpdate(user: User, productOrderUpdateRequest: ProductOrderUpdateRequest): ProductOrder {
        val productOrderId = productOrderUpdateRequest.productOrderId ?: error("Product id is required")
        val productOrder = getProductOrder(productOrderId) ?: error("No product order found for id: $productOrderId")

        val newStatus = when (productOrderUpdateRequest.updatedBy) {
            ProductOrderUpdatedBy.BY_SELLER -> ProductOrderStatus.PENDING_CUSTOMER_APPROVAL
            ProductOrderUpdatedBy.BY_CUSTOMER -> ProductOrderStatus.PENDING_SELLER_APPROVAL
        }

        val isOrderTransitionPossible = getIsOrderTransitionPossible(productOrder, newStatus)
        return if (isOrderTransitionPossible.transitionPossible) {
            val updatedProductOrder = saveOldStateAndUpdateProductOrder(productOrder, productOrderUpdateRequest)
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

        val productOrderAddress = productOrder.address ?: error("Product order does ot hav address")
        val productOrderCartItems = cartItemProvider.getCartItems(productOrder)

        val productOrderStateBeforeUpdate = ProductOrderStateBeforeUpdate(
            addressId = productOrderAddress.id,
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
                val productOrderUpdateByCustomerRequest = productOrderUpdateRequest as ProductOrderUpdateByCustomerRequest
                productOrderUpdateByCustomerRequest.newAddressId?.let {
                    if (productOrderAddress.id != it) {
                        val address = addressProvider.getAddress(it) ?: error("Address does not exist for id: $it")
                        val user = productOrder.addedBy ?: error("User does not exist for product order with id: ${productOrder.id}")
                        val isUserAddressValid = addressProvider.getIsUserAddressValid(user, address)
                        if (!isUserAddressValid) {
                            error("Address doe not belong to the user.")
                        }
                        productOrder.address = address
                    }
                }
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
        return refreshProductOrder(productOrder)
    }

    fun productOrderUpdateApproval(user: User, productOrderStatusUpdateRequest: ProductOrderStatusUpdateRequest): ProductOrder {
        val productOrder = getProductOrder(productOrderStatusUpdateRequest.productOrderId) ?: error("No product order found for id: ${productOrderStatusUpdateRequest.productOrderId}")

        val newStatus = when (productOrderStatusUpdateRequest.updatedBy) {
            ProductOrderUpdatedBy.BY_SELLER -> {
                when (productOrderStatusUpdateRequest.updateType) {
                    ProductOrderUpdateType.ACCEPT -> ProductOrderStatus.ACCEPTED_BY_SELLER
                    ProductOrderUpdateType.REJECT -> ProductOrderStatus.REJECTED_BY_SELLER
                    ProductOrderUpdateType.CANCEL -> ProductOrderStatus.CANCELLED_BY_SELLER
                }
            }
            ProductOrderUpdatedBy.BY_CUSTOMER -> {
                when (productOrderStatusUpdateRequest.updateType) {
                    ProductOrderUpdateType.ACCEPT -> ProductOrderStatus.ACCEPTED_BY_CUSTOMER
                    ProductOrderUpdateType.REJECT -> ProductOrderStatus.REJECTED_BY_CUSTOMER
                    ProductOrderUpdateType.CANCEL -> ProductOrderStatus.CANCELLED_BY_CUSTOMER
                }
            }
        }

        // TODO: Add user role level checks on who can update what
        //val productOrderUser = productOrder.addedBy ?: error("Product order is missing user. productOrderId: ${productOrder.id}")

        val isOrderTransitionPossible = getIsOrderTransitionPossible(productOrder, newStatus)
        return if (isOrderTransitionPossible.transitionPossible) {
            // Remove the update as all the pending update has been approved
            productOrder.productOrderStateBeforeUpdate = ""
            val updatedProductOrder = productOrderRepository.save(productOrder)
            transitionStateTo(updatedProductOrder, newStatus)
        } else {
            error(isOrderTransitionPossible.errorMessage)
        }
    }

    fun getProductOrderDetails(orderId: String): ProductOrderDetailsResponse {
        val productOrder = getProductOrder(orderId) ?: error("No order found for orderId: $orderId")
        return productOrder.toProductOrderDetailsResponse(cartItemProvider)
    }
}
