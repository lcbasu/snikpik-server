package com.dukaankhata.server.dao

import com.dukaankhata.server.entities.Company
import com.dukaankhata.server.entities.Product
import com.dukaankhata.server.entities.ProductVariant
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductVariantRepository : JpaRepository<ProductVariant?, String?> {
    fun findAllByProduct(product: Product): List<ProductVariant>
    fun findAllByCompany(company: Company): List<ProductVariant>
}
