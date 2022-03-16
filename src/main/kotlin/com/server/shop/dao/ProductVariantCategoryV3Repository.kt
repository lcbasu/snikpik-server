package com.server.shop.dao

import com.server.shop.entities.ProductVariantCategoryV3
import com.server.shop.entities.ProductVariantCategoryV3Key
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductVariantCategoryV3Repository : JpaRepository<ProductVariantCategoryV3?, ProductVariantCategoryV3Key?> {
}
