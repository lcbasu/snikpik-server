package com.server.shop.dao

import com.server.shop.entities.ProductOrderStateChangeV3
import com.server.shop.entities.ProductOrderV3
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductOrderStateChangeV3Repository : JpaRepository<ProductOrderStateChangeV3?, String?> {
    fun findAllByProductOrder(productOrder: ProductOrderV3): List<ProductOrderStateChangeV3>
}
