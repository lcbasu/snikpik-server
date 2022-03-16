package com.server.shop.dao

import com.server.shop.entities.ProductVariantV3
import com.server.shop.entities.ProductVariantVertical
import com.server.shop.entities.ProductVariantVerticalKey
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductVariantVerticalRepository : JpaRepository<ProductVariantVertical?, ProductVariantVerticalKey?> {
}
