package com.server.shop.dao

import com.server.shop.entities.ProductCategoryV3Entity
import com.server.shop.entities.ProductCategoryV3Key
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductCategoryV3Repository : JpaRepository<ProductCategoryV3Entity?, ProductCategoryV3Key?> {
}
