package com.server.shop.dao

import com.server.shop.entities.ProductOrderPaymentV3
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductOrderPaymentV3Repository : JpaRepository<ProductOrderPaymentV3?, String?> {
}
