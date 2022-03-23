package com.server.shop.dao

import com.server.shop.entities.BookmarkedProductsV3
import com.server.shop.entities.UserV3
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BookmarkedProductsV3Repository : JpaRepository<BookmarkedProductsV3?, String?> {
//    fun findAllByTypeAndAddedBy(type: ProductOrderType, addedBy: UserV3): List<ProductOrderV3>
//    fun findAllByTypeAndAddedByAndOrderStatus(type: ProductOrderType, addedBy: UserV3, orderStatus: ProductOrderStatusV3): List<ProductOrderV3>
//    fun findAllByAddedBy(user: UserV3): List<ProductOrderV3>
//    fun findAllByOrderStatusNotIn(orderStatusNotIn: Set<ProductOrderStatusV3>): List<ProductOrderV3>

//    fun findAllByAddedBy(user: UserV3): List<BookmarkedProductsV3>

    fun findAllByAddedByAndBookmarked(addedBy: UserV3, bookmarked: Boolean, pageable: Pageable): Slice<BookmarkedProductsV3>

}
