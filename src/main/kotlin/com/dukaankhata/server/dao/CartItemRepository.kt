package com.dukaankhata.server.dao

import com.dukaankhata.server.entities.CartItem
import com.dukaankhata.server.entities.Product
import com.dukaankhata.server.entities.ProductOrder
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface CartItemRepository : JpaRepository<CartItem?, String?> {
    @Query(value ="SELECT * FROM cart_item WHERE product_id IN :productIds and added_by_user_id = :userId", nativeQuery = true)
    fun getCartItemsForUserForProducts(
        @Param("userId") userId: String,
        @Param("productIds") productIds: Set<String>
    ): List<CartItem>

    /**
     * It should always return 0 or 1 results.
     * We need to add a constraint at DB level so that
     * this combination is always unique
     * */
    fun findAllByProductAndProductOrder(product: Product, productOrder: ProductOrder): List<CartItem>


    fun findAllByProductOrder(productOrder: ProductOrder): List<CartItem>
}
