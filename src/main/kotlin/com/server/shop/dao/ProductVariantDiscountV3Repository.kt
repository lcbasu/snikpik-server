package com.server.shop.dao

import com.server.shop.entities.ProductVariantDiscountKeyV3
import com.server.shop.entities.ProductVariantDiscountV3
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductVariantDiscountV3Repository : JpaRepository<ProductVariantDiscountV3?, ProductVariantDiscountKeyV3?>
