package com.server.shop.dao

import com.server.shop.entities.CartItemV3
import com.server.shop.entities.ProductOrderV3
import com.server.shop.entities.ProductVariantV3
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface CartItemV3Repository : JpaRepository<CartItemV3?, String?> {
    @Query(value ="SELECT * FROM cart_item_v3 WHERE product_variant_id IN :productVariantIds and added_by_user_id = :userId", nativeQuery = true)
    fun getCartItemsForUserForProductVariants(
        @Param("userId") userId: String,
        @Param("productVariantIds") productVariantIds: Set<String>
    ): List<CartItemV3>

    @Query(value ="SELECT * FROM cart_item_v3 WHERE product_id IN :productIds and added_by_user_id = :userId", nativeQuery = true)
    fun getCartItemsForUserForProducts(
        @Param("userId") userId: String,
        @Param("productIds") productIds: Set<String>
    ): List<CartItemV3>

    /**
     * It should always return 0 or 1 results.
     * We need to add a constraint at DB level so that
     * this combination is always unique
     * */
    fun findAllByProductVariantAndProductOrder(productVariant: ProductVariantV3, productOrder: ProductOrderV3): List<CartItemV3>


    fun findAllByProductOrder(productOrder: ProductOrderV3): List<CartItemV3>
    fun countAllByProductOrder(productOrder: ProductOrderV3): Long
}
