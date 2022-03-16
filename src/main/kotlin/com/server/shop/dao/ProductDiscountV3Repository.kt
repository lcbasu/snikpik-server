package com.server.shop.dao

import com.server.shop.entities.ProductDiscountKeyV3
import com.server.shop.entities.ProductDiscountV3
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductDiscountV3Repository : JpaRepository<ProductDiscountV3?, ProductDiscountKeyV3?>
