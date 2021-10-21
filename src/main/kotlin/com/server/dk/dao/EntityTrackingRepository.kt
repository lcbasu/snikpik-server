package com.server.dk.dao

import com.server.common.enums.TrackingType
import com.server.dk.entities.*
import com.server.dk.enums.EntityType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EntityTrackingRepository : JpaRepository<EntityTracking?, String?> {
    fun findAllByCompany(company: Company): List<EntityTracking>
    fun findAllByProduct(product: Product): List<EntityTracking>
    fun findAllByProductVariant(productVariant: ProductVariant): List<EntityTracking>
    fun findAllByAddedBy(user: User): List<EntityTracking>
    fun findAllByCompanyAndEntityTypeAndTrackingType(
        company: Company,
        entityType: EntityType,
        trackingType: TrackingType
    ): List<EntityTracking>
}
