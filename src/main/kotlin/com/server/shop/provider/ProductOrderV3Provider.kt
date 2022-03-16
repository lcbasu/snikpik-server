package com.server.shop.provider

import com.fasterxml.jackson.databind.ObjectMapper
import com.razorpay.RazorpayClient
import com.server.common.enums.ReadableIdPrefix
import com.server.common.provider.SecurityProvider
import com.server.common.provider.UniqueIdProvider
import com.server.shop.dao.ProductOrderV3Repository
import com.server.shop.dto.*
import com.server.shop.entities.AddressV3
import com.server.shop.entities.ProductOrderV3
import com.server.shop.entities.UserV3
import com.server.shop.enums.ProductOrderStatusV3
import com.server.shop.enums.ProductOrderType
import com.server.shop.model.OrderStateTransitionOutputV3
import com.server.shop.model.UpdatedCartDataV3
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ProductOrderV3Provider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var productOrderV3Repository: ProductOrderV3Repository

    @Autowired
    private lateinit var securityProvider: SecurityProvider


    @Autowired
    private lateinit var userV3Provider: UserV3Provider


    @Autowired
    private lateinit var productVariantV3Provider: ProductVariantV3Provider

    @Autowired
    private lateinit var uniqueIdProvider: UniqueIdProvider

    @Autowired
    private lateinit var cartItemsV3Provider: CartItemsV3Provider

    @Autowired
    private lateinit var productOrderStateChangeV3Provider: ProductOrderStateChangeV3Provider

    @Autowired
    private lateinit var saveForLaterProvider: SaveForLaterProvider

    @Autowired
    private lateinit var addressV3Provider: AddressV3Provider

    @Autowired
    private lateinit var razorpayClient: RazorpayClient

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var razorpayProvider: RazorpayProvider

    fun getProductOrderV3(productOrderId: String): ProductOrderV3? =
        try {
            productOrderV3Repository.findById(productOrderId).get()
        } catch (e: Exception) {
            e.printStackTrace()
            logger.error("Filed to get ProductOrderV3 for Id: $productOrderId")
            null
        }

    fun getProductOrders(type: ProductOrderType, user: UserV3, orderStatusIn: Set<ProductOrderStatusV3>) =
        productOrderV3Repository.findAllByTypeAndAddedByAndOrderStatusIn(type, user, orderStatusIn)


    fun getProductOrdersNotIn(types: Set<ProductOrderType>, user: UserV3, orderStatusNotIn: Set<ProductOrderStatusV3>) =
        productOrderV3Repository.findAllByTypeInAndAddedByAndOrderStatusNotIn(types, user, orderStatusNotIn)

    fun getAllOrdersForUser(user: UserV3) = getProductOrdersNotIn(
        setOf(ProductOrderType.REGULAR_ORDER, ProductOrderType.BUY_NOW_ORDER), user, setOf(
            ProductOrderStatusV3.DRAFT, ProductOrderStatusV3.ADDRESS_ADDED
        ))

    fun getActiveProductOrderBag(): ProductOrderV3? {
        val userV3 = userV3Provider.getUserV3FromLoggedInUser() ?: error("Logged in user not found")
        return getActiveProductOrderBag(userV3)
    }

    fun getAllOrdersForLoggedInUser(): List<ProductOrderV3> {
        val userV3 = userV3Provider.getUserV3FromLoggedInUser() ?: error("Logged in user not found")
        return getAllOrdersForUser(userV3)
    }

    fun getActiveProductOrderBag(user: UserV3): ProductOrderV3? {
        // Sometimes even after adding address you would want to add items to bag
        // Hence keeping both draft and address added state as active bag
        val draftOrders = getProductOrders(ProductOrderType.REGULAR_ORDER, user, setOf(ProductOrderStatusV3.DRAFT, ProductOrderStatusV3.ADDRESS_ADDED))

        if (draftOrders.size > 1) {
            error("There should be only one Cart active for a customer for each type of order")
        }

        if (draftOrders.size == 1) {
            return draftOrders.first()
        }
        return null
    }

    fun saveProductOrder(productOrder: ProductOrderV3): ProductOrderV3 {
        val oldOrder = getProductOrderV3(productOrder.id)
        val savedProductOrder = productOrderV3Repository.save(productOrder)
        productOrderStateChangeV3Provider.saveProductOrderStateChange(oldOrder, savedProductOrder);
        return savedProductOrder
    }

    fun saveAndRefreshProductOrder(productOrder: ProductOrderV3): ProductOrderV3 {
        val updateAllowedInState = setOf(
            ProductOrderStatusV3.DRAFT,
            ProductOrderStatusV3.ADDRESS_ADDED)

        if (updateAllowedInState.contains(productOrder.orderStatus)) {
            // Step 1: Reset the values
            productOrder.totalPricePayableInPaisa = 0

            productOrder.totalUnitsInAllCarts = 0
            productOrder.totalTaxInPaisa = 0
            productOrder.priceOfCartItemsWithoutTaxInPaisa = 0
            productOrder.totalDiscountInPaisa = 0

            // Step 2: Set the values again using the latest prices, update total items count ,and update delivery dates
            val cartItems = cartItemsV3Provider.getCartItems(productOrder)
            productOrder.totalCartItems = cartItems.size.toLong()
            cartItems.map { cartItem ->
                productOrder.totalUnitsInAllCarts += cartItem.totalUnits
                productOrder.totalTaxInPaisa += cartItem.totalTaxInPaisaPaid ?: 0
                productOrder.priceOfCartItemsWithoutTaxInPaisa += cartItem.totalPriceWithoutTaxInPaisaPaid ?: 0
                productOrder.totalDiscountInPaisa += cartItemsV3Provider.getDiscountInPaisa(cartItem)

                if (cartItem.maxDeliveryDateTime != null) {
                    if (productOrder.maxOfMaxDeliveryDateTime == null || cartItem.maxDeliveryDateTime!!.isAfter(productOrder.maxOfMaxDeliveryDateTime)) {
                        productOrder.maxOfMaxDeliveryDateTime = cartItem.maxDeliveryDateTime
                    }

                    if (productOrder.minOfMaxDeliveryDateTime == null || cartItem.maxDeliveryDateTime!!.isBefore(productOrder.minOfMaxDeliveryDateTime)) {
                        productOrder.minOfMaxDeliveryDateTime = cartItem.maxDeliveryDateTime
                    }
                }

                if (cartItem.promisedDeliveryDateTime != null) {
                    if (productOrder.maxOfPromisedDeliveryDateTime == null || cartItem.promisedDeliveryDateTime!!.isAfter(productOrder.maxOfPromisedDeliveryDateTime)) {
                        productOrder.maxOfPromisedDeliveryDateTime = cartItem.promisedDeliveryDateTime
                    }

                    if (productOrder.minOfPromisedDeliveryDateTime == null || cartItem.promisedDeliveryDateTime!!.isBefore(productOrder.minOfPromisedDeliveryDateTime)) {
                        productOrder.minOfPromisedDeliveryDateTime = cartItem.promisedDeliveryDateTime
                    }
                }
            }

            // Also check if the order address is null and the user default address has changed to non-null then assign it as delivery address
            productOrder.totalPricePayableInPaisa = (productOrder.priceOfCartItemsWithoutTaxInPaisa + productOrder.totalTaxInPaisa + productOrder.deliveryChargeInPaisa) - productOrder.totalDiscountInPaisa
            if (productOrder.deliveryAddress == null && productOrder.addedBy?.defaultAddress != null) {
                productOrder.deliveryAddress = productOrder.addedBy?.defaultAddress
            }

            return saveProductOrder(productOrder)
        } else {
            logger.error("Update not allowed in state: ${productOrder.orderStatus} for order: ${productOrder.id}")
            return productOrder
        }
    }

    fun updateCart(request: UpdateCartV3Request): UpdatedCartDataV3? {
        val userV3 = userV3Provider.getUserV3FromLoggedInUser() ?: error("Logged in user not found")

        val productVariant = productVariantV3Provider.getProductVariant(request.productVariantId) ?: error("Product Variant is required")
        val activeProductOrderBag = getOrCreateActiveProductOrderBag(userV3)
        return cartItemsV3Provider.updateCartAndDependentOrder(
            user = userV3,
            productVariant = productVariant,
            productOrder = activeProductOrderBag,
            cartItemUpdateAction = request.action,
            newQuantity = request.newQuantity)
    }

    fun getOrCreateActiveProductOrderBag(user: UserV3): ProductOrderV3 {
        return getActiveProductOrderBag(user) ?:
        createProductOrder(ProductOrderType.REGULAR_ORDER, user, ProductOrderStatusV3.DRAFT)
    }

    fun createProductOrder(type: ProductOrderType, user: UserV3, productOrderStatus: ProductOrderStatusV3): ProductOrderV3 {
        val newProductOrder = ProductOrderV3()
        // TODO: Update this to a bigger minLength once we start getting substantial number of orders
        newProductOrder.id = uniqueIdProvider.getUniqueIdAfterSaving(
            prefix = ReadableIdPrefix.ORD.name,
            onlyNumbers = true,
            minLength = 10,
            maxLength = 15)
        newProductOrder.addedBy = user
        newProductOrder.deliveryAddress = user.defaultAddress
        return saveProductOrder(newProductOrder)
    }

    fun updateStatus(request: ProductOrderStatusUpdateV3Request): ProductOrderV3? {
        TODO("Not yet implemented")
    }

    fun archiveClearedCartOrder(productOrder: ProductOrderV3): ProductOrderV3 {
        TODO("Not yet implemented")
    }

    fun clearCart(): ProductOrderV3? {
        val productOrder = getActiveProductOrderBag()
        if (productOrder != null) {
            // archive this order
            transitionStateTo(productOrder, ProductOrderStatusV3.ARCHIVED)
            // and create a new order
            return getOrCreateActiveProductOrderBag(productOrder.addedBy!!)
        }
        return null
    }


    private fun getIsOrderTransitionPossible(productOrder: ProductOrderV3, newStatus: ProductOrderStatusV3): OrderStateTransitionOutputV3 {
        if (productOrder.orderStatus == newStatus){
            val allowedSameStateChange = setOf(
                ProductOrderStatusV3.ADDRESS_ADDED,

                // Adding failed and cancelled status for payment and refund
                // as at the time of payment, payment can fail multiple times.
                ProductOrderStatusV3.PAYMENT_FAILED,
                ProductOrderStatusV3.PAYMENT_CANCELED,
                ProductOrderStatusV3.REFUND_FAILED)
            if (allowedSameStateChange.contains(newStatus)) {
                return OrderStateTransitionOutputV3(
                    transitionPossible = true
                )
            } else {
                error("Not required to move to $newStatus. Order is already in $newStatus state.")
            }
        }
        var acceptableOldState = setOf<ProductOrderStatusV3>()
        when (newStatus) {
            ProductOrderStatusV3.DRAFT -> TODO()
            ProductOrderStatusV3.ADDRESS_ADDED -> acceptableOldState = setOf(
                ProductOrderStatusV3.DRAFT,
                ProductOrderStatusV3.ADDRESS_ADDED,
            )
            ProductOrderStatusV3.PAYMENT_SUCCESS -> acceptableOldState = setOf(
                ProductOrderStatusV3.ADDRESS_ADDED,
            )
            ProductOrderStatusV3.PAYMENT_FAILED -> acceptableOldState = setOf(
                ProductOrderStatusV3.ADDRESS_ADDED,
            )
            ProductOrderStatusV3.PAYMENT_CANCELED -> acceptableOldState = setOf(
                ProductOrderStatusV3.ADDRESS_ADDED,
            )
            ProductOrderStatusV3.PLACED -> acceptableOldState = setOf(
                ProductOrderStatusV3.PAYMENT_SUCCESS,
            )
            ProductOrderStatusV3.CANCELED_BY_CUSTOMER -> TODO()
            ProductOrderStatusV3.CANCELED_BY_COMPANY -> TODO()
            ProductOrderStatusV3.IN_TRANSIT -> TODO()
            ProductOrderStatusV3.RETURNED_BY_DELIVERY -> TODO()
            ProductOrderStatusV3.OUT_FOR_DELIVERY -> TODO()
            ProductOrderStatusV3.NOT_ACCEPTED_ON_DELIVERY -> TODO()
            ProductOrderStatusV3.PARTIALLY_DELIVERED -> TODO()
            ProductOrderStatusV3.DELIVERED -> TODO()
            ProductOrderStatusV3.RETURNED_BY_CUSTOMER_FOR_REPLACEMENT -> TODO()
            ProductOrderStatusV3.RETURNED_BY_CUSTOMER_FOR_REFUND -> TODO()
            ProductOrderStatusV3.REFUNDED_INITIATED -> TODO()
            ProductOrderStatusV3.REFUNDED_SUCCESS -> TODO()
            ProductOrderStatusV3.REFUND_FAILED -> TODO()
            ProductOrderStatusV3.NEW_ORDER_STARTED_FOR_REPLACEMENT -> TODO()
            ProductOrderStatusV3.ARCHIVED -> acceptableOldState = setOf(
                ProductOrderStatusV3.DRAFT,
                ProductOrderStatusV3.ADDRESS_ADDED,
                ProductOrderStatusV3.PAYMENT_FAILED,
                ProductOrderStatusV3.PAYMENT_CANCELED)
            }
        return if (acceptableOldState.contains(productOrder.orderStatus)) {
            OrderStateTransitionOutputV3(
                transitionPossible = true
            )
        } else {
            val errorMessage = "Can only move to $newStatus, if the order is the acceptableOldState: ${acceptableOldState.toString()}"
            logger.error(errorMessage)
            OrderStateTransitionOutputV3(
                transitionPossible = false,
                errorMessage = errorMessage
            )
        }
    }

    fun transitionStateTo(productOrder: ProductOrderV3, newStatus: ProductOrderStatusV3): ProductOrderV3 {
        val isOrderTransitionPossible = getIsOrderTransitionPossible(productOrder, newStatus)
        return if (isOrderTransitionPossible.transitionPossible) {
            // TODO: Send notification to Seller and Customer based on transition
            productOrder.orderStatus = newStatus
            saveProductOrder(productOrder)
        } else {
            error(isOrderTransitionPossible.errorMessage)
        }
    }

    fun saveForLater(request: SaveForLaterRequest): UpdatedCartDataV3? {
        val cartItem = cartItemsV3Provider.getCartItem(request.cartItemId) ?: error("CartItem not found for id: ${request.cartItemId}")
        val productOrder = getProductOrderV3(request.productOrderId) ?: error("ProductOrder not found for id: ${request.productOrderId}")

        if (cartItem.productOrder!!.id != productOrder.id) error("CartItem ${cartItem.id} is not part of ProductOrder ${productOrder.id}")

        saveForLaterProvider.saveSaveForLater(cartItem) ?: error("Could not save cartItem ${cartItem.id} for later")

        val request = UpdateCartV3Request(
            productVariantId = cartItem.productVariant!!.id,
            type = productOrder.type,
            newQuantity = 0,
        )
        return updateCart(request)
    }

    fun updateDeliveryAddress(request: UpdateDeliveryAddressRequest): ProductOrderV3? {

        val productOrder = getProductOrderV3(request.productOrderId) ?: error("ProductOrder not found for id: ${request.productOrderId}")

        if (request.addressRequest != null && request.savedAddressId != null) {
            error("Can't update order: ${request.productOrderId} address when both address request and savedAddressId: ${request.savedAddressId} is present")
        }

        if (request.addressRequest == null && request.savedAddressId == null) {
            error("Can't update order: ${request.productOrderId} address when both address request and savedAddressId is absent")
        }

        var address: AddressV3? = null
        if (request.addressRequest != null) {
            address = addressV3Provider.saveAddress(productOrder.addedBy!!, request.addressRequest)
        } else if (request.savedAddressId != null) {
            address = addressV3Provider.getAddressV3(request.savedAddressId)
        }

        if (address == null) error("Could not find or create address for orderId: ${request.productOrderId} and request: $request")


        productOrder.deliveryAddress = address

        // Save updated address and also refresh the prices before checkout
        val updatedOrder = saveAndRefreshProductOrder(productOrder)

        // Change the state of the order
        return transitionStateTo(updatedOrder, ProductOrderStatusV3.ADDRESS_ADDED)
    }

    fun createPaymentOrder(request: CreatePaymentOrderRequest): CreatePaymentOrderResponse? {
        val productOrder = getProductOrderV3(request.productOrderId) ?: error("ProductOrder not found for id: ${request.productOrderId}")

        // If the order is still in draft state, and has a default address then move the order to the ADDRESS_ADDED state
        val orderWithAddress = if (productOrder.orderStatus == ProductOrderStatusV3.DRAFT && productOrder.deliveryAddress != null) {
            transitionStateTo(productOrder, ProductOrderStatusV3.ADDRESS_ADDED)
        } else {
            productOrder
        }

        if (orderWithAddress.orderStatus != ProductOrderStatusV3.ADDRESS_ADDED) {
            error("Can't create payment order for order: ${request.productOrderId} with status: ${orderWithAddress.orderStatus}")
        }

        val razorpayOrderResponse = razorpayProvider.getRazorPayOrderResponse(orderWithAddress)

        if (razorpayOrderResponse == null) {
            error("Could not create razorpay order for orderId: ${request.productOrderId} and request: $request")
        }

        orderWithAddress.razorpayOrderId = razorpayOrderResponse.id
        val updatedOrder = saveProductOrder(orderWithAddress)

        return CreatePaymentOrderResponse(
            productOrder = updatedOrder.toSavedProductOrderV3Response(),
            razorpayOrderResponse = razorpayOrderResponse,
        )
    }

    @Transactional
    fun verifyAndCommitPayment(request: VerifyAndCommitPaymentRequest): VerifyAndCommitPaymentResponse? {
        val productOrder = getProductOrderV3(request.productOrderId) ?: error("ProductOrder not found for id: ${request.productOrderId}")

        if (productOrder.orderStatus != ProductOrderStatusV3.ADDRESS_ADDED) {
            error("Can't verify and commit payment order for order: ${request.productOrderId} with status: ${productOrder.orderStatus}")
        }

        if (productOrder.razorpayOrderId == null) {
            error("Could not find razorpay order for productOrderId: ${request.productOrderId}")
        }

        if (productOrder.razorpayOrderId != request.razorpayOrderId) {
            error("Razorpay razorpayOrderId: ${request.razorpayOrderId} in request does not match with the one saved for product order: ${productOrder.razorpayOrderId}")
        }

        val verified = razorpayProvider.verifyOrderPaymentSignature(
            expectedSignature = request.razorpaySignature,
            orderId = request.razorpayOrderId,
            paymentId = request.razorpayPaymentId,
        )

        if (!verified) {
            return VerifyAndCommitPaymentResponse(
                verified = false,
                productOrder = productOrder.toSavedProductOrderV3Response(),
            )
        } else {

            val updatedOrder = transitionStateTo(productOrder, ProductOrderStatusV3.PAYMENT_SUCCESS)

            updatedOrder.razorpayPaymentId = request.razorpayPaymentId
            val updatedOrderWithPayment = saveProductOrder(updatedOrder)
            val updatedOrderAfterPlace = transitionStateTo(updatedOrderWithPayment, ProductOrderStatusV3.PLACED)
            return VerifyAndCommitPaymentResponse(
                verified = true,
                productOrder = updatedOrderAfterPlace.toSavedProductOrderV3Response(),
            )
        }
    }

}
