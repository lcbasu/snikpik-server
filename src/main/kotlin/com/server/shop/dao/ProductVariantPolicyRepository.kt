package com.server.shop.dao

import com.server.shop.entities.ProductPolicy
import com.server.shop.entities.ProductPolicyKey
import com.server.shop.entities.ProductVariantPolicy
import com.server.shop.entities.ProductVariantPolicyKey
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductVariantPolicyRepository : JpaRepository<ProductVariantPolicy?, ProductVariantPolicyKey?> {
}
