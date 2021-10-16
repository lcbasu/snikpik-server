package com.server.dk.provider

import MigratedCartData
import UpdatedCartData
import com.server.dk.dao.CartItemRepository
import com.server.dk.entities.*
import com.server.dk.enums.CartItemUpdateAction
import com.server.dk.enums.ReadableIdPrefix
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class CartItemProvider {

    @Autowired
    private lateinit var cartItemRepository: CartItemRepository

    @Autowired
    private lateinit var productOrderProvider: ProductOrderProvider

    @Autowired
    private lateinit var uniqueIdProvider: UniqueIdProvider

    fun getCartItem(cartItemId: String): CartItem? =
        try {
            cartItemRepository.findById(cartItemId).get()
        } catch (e: Exception) {
            null
        }

    fun getCartItemsForUserForProductVariants(userId: String, productVariantIds: Set<String>): List<CartItem> =
        try {
            cartItemRepository.getCartItemsForUserForProductVariants(userId, productVariantIds)
        } catch (e: Exception) {
            emptyList()
        }

    fun getCartItemsForUserForProducts(userId: String, productIds: Set<String>): List<CartItem> =
        try {
            cartItemRepository.getCartItemsForUserForProducts(userId, productIds)
        } catch (e: Exception) {
            emptyList()
        }

    fun getCartItems(productOrder: ProductOrder) =
        cartItemRepository.findAllByProductOrder(productOrder)

    fun getCartItemsCount(productOrder: ProductOrder) =
        cartItemRepository.countAllByProductOrder(productOrder)

    fun getCartItem(productVariant: ProductVariant, productOrder: ProductOrder): CartItem? {
        val cartItems = cartItemRepository.findAllByProductVariantAndProductOrder(productVariant, productOrder)
        if (cartItems.size > 1) {
            error("Single cart items should always have ONLY 1 product to product-order combination")
        }
        return cartItems.firstOrNull()
    }

    fun createNewCartItem(company: Company,
                          user: User,
                          productVariant: ProductVariant,
                          productOrder: ProductOrder): CartItem {
        val newCartItem = CartItem()

        newCartItem.id = uniqueIdProvider.getUniqueId(ReadableIdPrefix.CRT.name)

        newCartItem.productVariant = productVariant
        newCartItem.product = productVariant.product
        newCartItem.productOrder = productOrder

        newCartItem.totalUnits = 1

        newCartItem.taxPerUnitInPaisa = productVariant.taxPerUnitInPaisa
        newCartItem.pricePerUnitInPaisa = productVariant.sellingPricePerUnitInPaisa
        newCartItem.totalTaxInPaisa = productVariant.taxPerUnitInPaisa * newCartItem.totalUnits
        newCartItem.totalPriceWithoutTaxInPaisa = productVariant.sellingPricePerUnitInPaisa * newCartItem.totalUnits

        newCartItem.company = company
        newCartItem.addedBy = user

        return cartItemRepository.save(newCartItem)
    }

    fun updateProductInCart(cartItem: CartItem, cartItemUpdateAction: CartItemUpdateAction?, newQuantity: Long?): CartItem {

        if (cartItemUpdateAction == null && newQuantity == null) {
            error("Both cartItemUpdateAction & newQuantity can not be null at the same time")
        }

        if (cartItem.totalUnits == 0L && cartItemUpdateAction == CartItemUpdateAction.REMOVE) {
            error("Can not remove already removed items from active bag")
        }

        // Default
        val defaultUnitsToAdd = 1

        val symbol = when (cartItemUpdateAction) {
            CartItemUpdateAction.ADD -> 1
            CartItemUpdateAction.REMOVE -> -1
            else -> 1
        }

        val newCount = newQuantity ?: (cartItem.totalUnits + defaultUnitsToAdd.times(symbol))
        return updateProductInCart(cartItem, newCount)
    }

    @Transactional
    fun updateCartAndDependentOrder(company: Company,
                                    user: User,
                                    productVariant: ProductVariant,
                                    productOrder: ProductOrder,
                                    cartItemUpdateAction: CartItemUpdateAction?,
                                    newQuantity: Long?): UpdatedCartData {
        val updatedCartItem = updateCart(
            company = company,
            user = user,
            productVariant = productVariant,
            productOrder = productOrder,
            cartItemUpdateAction = cartItemUpdateAction,
            newQuantity = newQuantity
        )
        val productOrderCartItems = getCartItems(productOrder)
        val updatedProductOrder = productOrderProvider.saveAndRefreshProductOrder(productOrder)
        return UpdatedCartData(
            updatedCartItem = updatedCartItem,
            updatedProductOrder = updatedProductOrder,
            productOrderCartItems = productOrderCartItems
        )
    }

    private fun updateCart(company: Company, user: User, productVariant: ProductVariant, productOrder: ProductOrder, cartItemUpdateAction: CartItemUpdateAction?, newQuantity: Long?): CartItem {
        val existingCartItem = getCartItem(productVariant = productVariant, productOrder = productOrder)
        return if (existingCartItem == null) {
            // Means this product is not added yet, so we need to add it
            if (cartItemUpdateAction == CartItemUpdateAction.REMOVE) {
                error("Nothing to remove from the active bag")
            }
            createNewCartItem(
                company = company,
                user = user,
                productVariant = productVariant,
                productOrder = productOrder
            )

        } else {
            updateProductInCart(
                cartItem = existingCartItem,
                cartItemUpdateAction = cartItemUpdateAction,
                newQuantity = newQuantity
            )
        }
    }

    fun migrateCart(fromProductOrder: ProductOrder, toProductOrder: ProductOrder): MigratedCartData {
        val fromProductOrderCartItems = getCartItems(fromProductOrder)
        val migratedCartItems = migrateCartItems(fromProductOrderCartItems, toProductOrder)
        val updatedProductOrder = productOrderProvider.saveAndRefreshProductOrder(toProductOrder)
        return MigratedCartData(
            fromProductOrder = fromProductOrder,
            toProductOrder = updatedProductOrder,
            migratedCartItems = migratedCartItems
        )
    }

    private fun migrateCartItems(fromProductOrderCartItems: List<CartItem>, toProductOrder: ProductOrder): List<CartItem> {
        return fromProductOrderCartItems.map { cartItem ->
            cartItem.addedBy = toProductOrder.addedBy
            cartItem.productOrder = toProductOrder
            cartItemRepository.saveAndFlush(cartItem)
        }
    }

    fun updateProductInCart(cartItemId: String, newCount: Long): CartItem {
        val cartItem = getCartItem(cartItemId) ?: error("Unable to get cart items for id: $cartItemId")
        return updateProductInCart(cartItem, newCount)
    }

    private fun updateProductInCart(cartItem: CartItem, newCount: Long): CartItem {
        if (newCount <= 0L) {
            cartItem.totalUnits = 0
            cartItem.taxPerUnitInPaisa = 0
            cartItem.pricePerUnitInPaisa = 0
            cartItem.totalTaxInPaisa = 0
            cartItem.totalPriceWithoutTaxInPaisa = 0
        } else {
            // TODO: Fix This.
            // Right now if the product price is updated while the order is being updated,
            // The customer will see a different price for cart item than what he actually paid for
            val productVariant = cartItem.productVariant ?: error("Cart Items should always have product variant")
            cartItem.totalUnits = newCount
            cartItem.taxPerUnitInPaisa = productVariant.taxPerUnitInPaisa
            cartItem.pricePerUnitInPaisa = productVariant.sellingPricePerUnitInPaisa
            cartItem.totalTaxInPaisa = productVariant.taxPerUnitInPaisa * cartItem.totalUnits
            cartItem.totalPriceWithoutTaxInPaisa = productVariant.sellingPricePerUnitInPaisa * cartItem.totalUnits
        }

        return cartItemRepository.save(cartItem)
    }
}
