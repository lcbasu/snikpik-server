package com.server.shop.dao

import com.server.shop.entities.DiscountV3
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DiscountV3Repository : JpaRepository<DiscountV3?, String?> {
}
