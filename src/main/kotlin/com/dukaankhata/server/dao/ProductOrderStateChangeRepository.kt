package com.dukaankhata.server.dao

import com.dukaankhata.server.entities.ProductOrder
import com.dukaankhata.server.entities.ProductOrderStateChange
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductOrderStateChangeRepository : JpaRepository<ProductOrderStateChange?, String?> {
    fun findAllByProductOrder(productOrder: ProductOrder): List<ProductOrderStateChange>
}
