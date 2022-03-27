package com.server.shop.provider

import com.server.common.enums.ReadableIdPrefix
import com.server.common.provider.UniqueIdProvider
import com.server.common.utils.DateUtils
import com.server.shop.dao.CartItemV3Repository
import com.server.shop.entities.CartItemV3
import com.server.shop.entities.ProductOrderV3
import com.server.shop.entities.ProductVariantV3
import com.server.shop.entities.UserV3
import com.server.shop.enums.CartItemUpdateActionV3
import com.server.shop.model.UpdatedCartDataV3
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class CartItemsV3Provider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var cartItemV3Repository: CartItemV3Repository

    @Autowired
    private lateinit var productOrderV3Provider: ProductOrderV3Provider

    @Autowired
    private lateinit var uniqueIdProvider: UniqueIdProvider

    fun getCartItem(cartItemId: String): CartItemV3? =
        try {
            cartItemV3Repository.findById(cartItemId).get()
        } catch (e: Exception) {
            null
        }

    fun getCartItemsForUserForProductVariants(userId: String, productVariantIds: Set<String>): List<CartItemV3> =
        try {
            cartItemV3Repository.getCartItemsForUserForProductVariants(userId, productVariantIds)
        } catch (e: Exception) {
            emptyList()
        }

    fun getCartItemsForUserForProducts(userId: String, productIds: Set<String>): List<CartItemV3> =
        try {
            cartItemV3Repository.getCartItemsForUserForProducts(userId, productIds)
        } catch (e: Exception) {
            emptyList()
        }

    fun getCartItems(productOrder: ProductOrderV3?) =
        productOrder?.let { cartItemV3Repository.findAllByProductOrder(it) } ?: emptyList()

    fun getCartItemsCount(productOrder: ProductOrderV3) =
        cartItemV3Repository.countAllByProductOrder(productOrder)

    fun getCartItem(productVariant: ProductVariantV3, productOrder: ProductOrderV3): CartItemV3? {
        val cartItems = cartItemV3Repository.findAllByProductVariantAndProductOrder(productVariant, productOrder)
        if (cartItems.size > 1) {
            error("Single cart items should always have ONLY 1 product to product-order combination")
        }
        return cartItems.firstOrNull()
    }

    fun createNewCartItem(user: UserV3,
                          productVariant: ProductVariantV3,
                          productOrder: ProductOrderV3,
                          newQuantity: Long?): CartItemV3 {
        val newCartItem = CartItemV3()

        newCartItem.id = uniqueIdProvider.getUniqueIdAfterSaving(ReadableIdPrefix.CRT.name)

        newCartItem.productVariant = productVariant
        newCartItem.product = productVariant.product
        newCartItem.productOrder = productOrder

        newCartItem.totalUnits = newQuantity ?: 1

//        newCartItem.taxPerUnitInPaisa = productVariant.taxPerUnitInPaisa
//        newCartItem.pricePerUnitInPaisa = productVariant.sellingPricePerUnitInPaisa
//        newCartItem.totalTaxInPaisa = productVariant.taxPerUnitInPaisa * newCartItem.totalUnits
//        newCartItem.totalPriceWithoutTaxInPaisa = productVariant.sellingPricePerUnitInPaisa * newCartItem.totalUnits

        newCartItem.taxPerUnitInPaisaPaid = 0
        newCartItem.totalTaxInPaisaPaid = 0

        newCartItem.pricePerUnitInPaisaPaid = productVariant.sellingPricePerUnitInPaisa
        newCartItem.totalPriceWithoutTaxInPaisaPaid = newCartItem.totalUnits * productVariant.sellingPricePerUnitInPaisa

        newCartItem.totalMrpInPaisa = newCartItem.totalUnits * productVariant.mrpPerUnitInPaisa
        newCartItem.totalSellingPriceInPaisa = newCartItem.totalUnits * productVariant.sellingPricePerUnitInPaisa

        // For now, promised delivery date is same as max delivery date
        newCartItem.maxDeliveryDateTime = DateUtils.addSecondsToNow(productVariant.maxDeliveryTimeInSeconds)
        newCartItem.promisedDeliveryDateTime = newCartItem.maxDeliveryDateTime

//        newCartItem.company = company
        newCartItem.addedBy = user

        return cartItemV3Repository.save(newCartItem)
    }

    fun updateProductInCart(cartItem: CartItemV3, cartItemUpdateAction: CartItemUpdateActionV3?, newQuantity: Long?): CartItemV3 {

        if (cartItemUpdateAction == null && newQuantity == null) {
            error("Both cartItemUpdateAction & newQuantity can not be null at the same time")
        }

        if (cartItem.totalUnits == 0L && cartItemUpdateAction == CartItemUpdateActionV3.REMOVE) {
            error("Can not remove already removed items from active bag")
        }

        // Default
        val defaultUnitsToAdd = 1

        val symbol = when (cartItemUpdateAction) {
            CartItemUpdateActionV3.ADD -> 1
            CartItemUpdateActionV3.REMOVE -> -1
            else -> 1
        }

        val newCount = newQuantity ?: (cartItem.totalUnits + defaultUnitsToAdd.times(symbol))
        return updateProductInCart(cartItem, newCount)
    }

    @Transactional
    fun updateCartAndDependentOrder(user: UserV3,
                                    productVariant: ProductVariantV3,
                                    productOrder: ProductOrderV3,
                                    cartItemUpdateAction: CartItemUpdateActionV3?,
                                    newQuantity: Long?): UpdatedCartDataV3 {
        val updatedCartItem = updateCart(
            user = user,
            productVariant = productVariant,
            productOrder = productOrder,
            cartItemUpdateAction = cartItemUpdateAction,
            newQuantity = newQuantity
        )
        val productOrderCartItems = getCartItems(productOrder)
        val updatedProductOrder = productOrderV3Provider.saveAndRefreshProductOrder(productOrder)
        return UpdatedCartDataV3(
            updatedCartItem = updatedCartItem,
            updatedProductOrder = updatedProductOrder,
            productOrderCartItems = productOrderCartItems
        )
    }

    private fun updateCart(user: UserV3, productVariant: ProductVariantV3, productOrder: ProductOrderV3, cartItemUpdateAction: CartItemUpdateActionV3?, newQuantity: Long?): CartItemV3 {
        val existingCartItem = getCartItem(productVariant = productVariant, productOrder = productOrder)
        return if (existingCartItem == null) {
            // Means this product is not added yet, so we need to add it
            if (cartItemUpdateAction == CartItemUpdateActionV3.REMOVE) {
                error("Nothing to remove from the active bag")
            }
            createNewCartItem(
                user = user,
                productVariant = productVariant,
                productOrder = productOrder,
                newQuantity = newQuantity
            )

        } else {
            updateProductInCart(
                cartItem = existingCartItem,
                cartItemUpdateAction = cartItemUpdateAction,
                newQuantity = newQuantity
            )
        }
    }

    fun updateProductInCart(cartItemId: String, newCount: Long): CartItemV3 {
        val cartItem = getCartItem(cartItemId) ?: error("Unable to get cart items for id: $cartItemId")
        return updateProductInCart(cartItem, newCount)
    }

    private fun updateProductInCart(cartItem: CartItemV3, newCount: Long): CartItemV3 {
        if (newCount <= 0L) {
            cartItem.totalUnits = 0

            cartItem.taxPerUnitInPaisaPaid = 0
            cartItem.totalTaxInPaisaPaid = 0

            cartItem.pricePerUnitInPaisaPaid = 0
            cartItem.totalPriceWithoutTaxInPaisaPaid = 0

            cartItem.totalMrpInPaisa = 0
            cartItem.totalSellingPriceInPaisa = 0
        } else {
            // TODO: Fix This.
            // Right now if the product price is updated while the order is being updated,
            // The customer will see a different price for cart item than what he actually paid for
            val productVariant = cartItem.productVariant ?: error("Cart Items should always have product variant")
            cartItem.totalUnits = newCount

            cartItem.taxPerUnitInPaisaPaid = 0
            cartItem.totalTaxInPaisaPaid = 0

            cartItem.pricePerUnitInPaisaPaid = productVariant.sellingPricePerUnitInPaisa
            cartItem.totalPriceWithoutTaxInPaisaPaid = newCount * productVariant.sellingPricePerUnitInPaisa

            cartItem.totalMrpInPaisa = newCount * productVariant.mrpPerUnitInPaisa
            cartItem.totalSellingPriceInPaisa = newCount * productVariant.sellingPricePerUnitInPaisa

            // For now, promised delivery date is same as max delivery date
            cartItem.maxDeliveryDateTime = DateUtils.addSecondsToNow(cartItem.productVariant!!.maxDeliveryTimeInSeconds)
            cartItem.promisedDeliveryDateTime = cartItem.maxDeliveryDateTime
        }

        return cartItemV3Repository.save(cartItem)
    }

//    fun getDiscountInPaisa(cartItem: CartItemV3): Long {
//        return cartItem.totalUnits * (cartItem.productVariant!!.mrpPerUnitInPaisa - cartItem.productVariant!!.sellingPricePerUnitInPaisa)
//    }

}
