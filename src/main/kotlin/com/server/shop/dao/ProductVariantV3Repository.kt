package com.server.shop.dao

import com.server.shop.entities.ProductV3
import com.server.shop.entities.ProductVariantV3
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductVariantV3Repository : JpaRepository<ProductVariantV3?, String?> {
    fun findAllByProduct(product: ProductV3): List<ProductVariantV3>
}
