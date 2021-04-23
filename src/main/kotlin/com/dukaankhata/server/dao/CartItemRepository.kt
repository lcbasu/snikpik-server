package com.dukaankhata.server.dao

import com.dukaankhata.server.entities.CartItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface CartItemRepository : JpaRepository<CartItem?, Long?> {
    @Query(value ="SELECT * FROM cart_item WHERE product_id IN :productIds and added_by_user_id = :userId", nativeQuery = true)
    fun getCartItemsForUserForProducts(
        @Param("userId") userId: String,
        @Param("productIds") productIds: Set<String>
    ): List<CartItem>
}
