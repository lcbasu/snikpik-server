package com.dukaankhata.server.utils

import com.dukaankhata.server.dao.CartItemRepository
import com.dukaankhata.server.entities.CartItem
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class CartItemUtils {

    @Autowired
    private lateinit var cartItemRepository: CartItemRepository

    fun getCartItemsForUserForProducts(userId: String, productIds: Set<String>): List<CartItem> =
        try {
            cartItemRepository.getCartItemsForUserForProducts(userId, productIds)
        } catch (e: Exception) {
            emptyList()
        }
}
