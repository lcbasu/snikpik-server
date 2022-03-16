package com.server.shop.dao

import com.server.shop.entities.CouponV3
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface CouponV3Repository : JpaRepository<CouponV3?, String?> {
}
