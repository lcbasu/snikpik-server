package com.server.shop.dao

import com.server.shop.entities.BookmarkedProductsV3
import com.server.shop.entities.ProductOrderV3
import com.server.shop.entities.UserV3
import com.server.shop.enums.ProductOrderStatusV3
import com.server.shop.enums.ProductOrderType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BookmarkedProductsV3Repository : JpaRepository<BookmarkedProductsV3?, String?> {
//    fun findAllByTypeAndAddedBy(type: ProductOrderType, addedBy: UserV3): List<ProductOrderV3>
//    fun findAllByTypeAndAddedByAndOrderStatus(type: ProductOrderType, addedBy: UserV3, orderStatus: ProductOrderStatusV3): List<ProductOrderV3>
//    fun findAllByAddedBy(user: UserV3): List<ProductOrderV3>
//    fun findAllByOrderStatusNotIn(orderStatusNotIn: Set<ProductOrderStatusV3>): List<ProductOrderV3>

    fun findAllByAddedBy(user: UserV3): List<BookmarkedProductsV3>

}
