package com.server.shop.dao

import com.server.shop.entities.BrandDiscount
import com.server.shop.entities.BrandDiscountKey
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BrandDiscountRepository : JpaRepository<BrandDiscount?, BrandDiscountKey?> {
    fun findAllByBrandId(brandId: String): List<BrandDiscount>
}
