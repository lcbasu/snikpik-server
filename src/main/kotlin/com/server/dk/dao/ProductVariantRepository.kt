package com.server.dk.dao

import com.server.dk.entities.Company
import com.server.dk.entities.Product
import com.server.dk.entities.ProductVariant
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductVariantRepository : JpaRepository<ProductVariant?, String?> {
    fun findAllByProduct(product: Product): List<ProductVariant>
    fun findAllByCompany(company: Company): List<ProductVariant>
}
