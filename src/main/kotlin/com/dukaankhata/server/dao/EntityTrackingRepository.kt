package com.dukaankhata.server.dao

import com.dukaankhata.server.entities.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EntityTrackingRepository : JpaRepository<EntityTracking?, String?> {
    fun findAllByCompany(company: Company): List<EntityTracking>
    fun findAllByProduct(product: Product): List<EntityTracking>
    fun findAllByProductVariant(productVariant: ProductVariant): List<EntityTracking>
    fun findAllByAddedBy(user: User): List<EntityTracking>
}
