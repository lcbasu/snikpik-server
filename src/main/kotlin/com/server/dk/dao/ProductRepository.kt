package com.server.dk.dao

import com.server.dk.entities.Company
import com.server.dk.entities.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository : JpaRepository<Product?, String?> {
    fun findAllByCompany(company: Company): List<Product>
}
