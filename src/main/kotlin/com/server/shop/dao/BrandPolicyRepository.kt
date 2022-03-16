package com.server.shop.dao

import com.server.shop.entities.BrandPolicy
import com.server.shop.entities.BrandPolicyKey
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BrandPolicyRepository : JpaRepository<BrandPolicy?, BrandPolicyKey?> {
    fun findAllByBrandId(brandId: String): List<BrandPolicy>
}
