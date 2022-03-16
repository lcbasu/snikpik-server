package com.server.shop.dao

import com.server.shop.entities.ProductPolicy
import com.server.shop.entities.ProductPolicyKey
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductPolicyRepository : JpaRepository<ProductPolicy?, ProductPolicyKey?> {
}
