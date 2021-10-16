package com.server.dk.dao

import com.server.dk.entities.ProductOrder
import com.server.dk.entities.ProductOrderStateChange
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductOrderStateChangeRepository : JpaRepository<ProductOrderStateChange?, String?> {
    fun findAllByProductOrder(productOrder: ProductOrder): List<ProductOrderStateChange>
}
