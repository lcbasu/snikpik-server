package com.server.shop.dao

import com.server.shop.entities.ProductOrderV3
import com.server.shop.entities.UserV3
import com.server.shop.enums.ProductOrderStatusV3
import com.server.shop.enums.ProductOrderType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductOrderV3Repository : JpaRepository<ProductOrderV3?, String?> {
    fun findAllByTypeAndAddedBy(type: ProductOrderType, addedBy: UserV3): List<ProductOrderV3>
    fun findAllByTypeAndAddedByAndOrderStatusIn(type: ProductOrderType, addedBy: UserV3, orderStatusIn: Set<ProductOrderStatusV3>): List<ProductOrderV3>
    fun findAllByTypeInAndAddedByAndOrderStatusNotIn(types: Set<ProductOrderType>, addedBy: UserV3, orderStatusNotIn: Set<ProductOrderStatusV3>): List<ProductOrderV3>
    fun findAllByAddedBy(user: UserV3): List<ProductOrderV3>
}
