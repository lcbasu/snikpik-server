package com.dukaankhata.server.utils

import MigratedCartData
import UpdatedCartData
import com.dukaankhata.server.dao.CartItemRepository
import com.dukaankhata.server.entities.*
import com.dukaankhata.server.enums.CartItemUpdateAction
import com.dukaankhata.server.enums.ReadableIdPrefix
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class CartItemUtils {

    @Autowired
    private lateinit var cartItemRepository: CartItemRepository

    @Autowired
    private lateinit var productOrderUtils: ProductOrderUtils

    @Autowired
    private lateinit var uniqueIdGeneratorUtils: UniqueIdGeneratorUtils

    fun getCartItem(cartItemId: String): CartItem? =
        try {
            cartItemRepository.findById(cartItemId).get()
        } catch (e: Exception) {
            null
        }

    fun getCartItemsForUserForProducts(userId: String, productIds: Set<String>): List<CartItem> =
        try {
            cartItemRepository.getCartItemsForUserForProducts(userId, productIds)
        } catch (e: Exception) {
            emptyList()
        }

    fun getCartItems(productOrder: ProductOrder) =
        cartItemRepository.findAllByProductOrder(productOrder)

    fun getCartItem(product: Product, productOrder: ProductOrder): CartItem? {
        val cartItems = cartItemRepository.findAllByProductAndProductOrder(product, productOrder)
        if (cartItems.size > 1) {
            error("Single cart items should always have ONLY 1 product to product-order combination")
        }
        return cartItems.firstOrNull()
    }

    fun createNewCartItem(company: Company,
                          user: User,
                          product: Product,
                          productOrder: ProductOrder): CartItem {
        val newCartItem = CartItem()

        newCartItem.id = uniqueIdGeneratorUtils.getUniqueId(ReadableIdPrefix.CRT.name)

        newCartItem.product = product
        newCartItem.productOrder = productOrder

        newCartItem.totalUnits = 1

        newCartItem.taxPerUnitInPaisa = product.taxPerUnitInPaisa
        newCartItem.pricePerUnitInPaisa = product.pricePerUnitInPaisa
        newCartItem.totalTaxInPaisa = product.taxPerUnitInPaisa * newCartItem.totalUnits
        newCartItem.totalPriceWithoutTaxInPaisa = product.pricePerUnitInPaisa * newCartItem.totalUnits

        newCartItem.company = company
        newCartItem.addedBy = user

        return cartItemRepository.save(newCartItem)
    }

    fun updateProductInCart(cartItem: CartItem, cartItemUpdateAction: CartItemUpdateAction): CartItem {

        if (cartItem.totalUnits == 0L && cartItemUpdateAction == CartItemUpdateAction.REMOVE) {
            error("Can not remove already removed items from active bag")
        }

        val unitsToAdd = when (cartItemUpdateAction) {
            CartItemUpdateAction.ADD -> 1
            CartItemUpdateAction.REMOVE -> -1
        }
        val newCount = cartItem.totalUnits + unitsToAdd
        return updateProductInCart(cartItem, newCount)
    }

    @Transactional
    fun updateCartAndDependentOrder(company: Company,
                                    user: User,
                                    product: Product,
                                    productOrder: ProductOrder,
                                    cartItemUpdateAction: CartItemUpdateAction): UpdatedCartData {
        val updatedCartItem = updateCart(
            company = company,
            user = user,
            product = product,
            productOrder = productOrder,
            cartItemUpdateAction = cartItemUpdateAction
        )
        val productOrderCartItems = getCartItems(productOrder)
        val updatedProductOrder = productOrderUtils.updateProductOrder(productOrder, productOrderCartItems)
        return UpdatedCartData(
            updatedCartItem = updatedCartItem,
            updatedProductOrder = updatedProductOrder,
            productOrderCartItems = productOrderCartItems
        )
    }

    private fun updateCart(company: Company, user: User, product: Product, productOrder: ProductOrder, cartItemUpdateAction: CartItemUpdateAction): CartItem {
        val existingCartItem = getCartItem(product = product, productOrder = productOrder)
        return if (existingCartItem == null) {
            // Means this product is not added yet, so we need to add it
            if (cartItemUpdateAction == CartItemUpdateAction.REMOVE) {
                error("Nothing to remove from the active bag")
            }
            createNewCartItem(
                company = company,
                user = user,
                product = product,
                productOrder = productOrder
            )

        } else {
            updateProductInCart(
                cartItem = existingCartItem,
                cartItemUpdateAction = cartItemUpdateAction
            )
        }
    }

    fun migrateCart(fromProductOrder: ProductOrder, toProductOrder: ProductOrder): MigratedCartData {
        val fromProductOrderCartItems = getCartItems(fromProductOrder)
        val migratedCartItems = migrateCartItems(fromProductOrderCartItems, toProductOrder)
        val updatedProductOrder = productOrderUtils.updateProductOrder(toProductOrder, fromProductOrderCartItems)
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
            val product = cartItem.product ?: error("Cart Items should always have product")
            cartItem.totalUnits = newCount
            cartItem.taxPerUnitInPaisa = product.taxPerUnitInPaisa
            cartItem.pricePerUnitInPaisa = product.pricePerUnitInPaisa
            cartItem.totalTaxInPaisa = product.taxPerUnitInPaisa * cartItem.totalUnits
            cartItem.totalPriceWithoutTaxInPaisa = product.pricePerUnitInPaisa * cartItem.totalUnits
        }

        return cartItemRepository.save(cartItem)
    }
}
