package com.server.shop.dao

import com.server.shop.entities.ProductV3
import com.server.shop.entities.UserV3
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductV3Repository : JpaRepository<ProductV3?, String?> {
    fun findAllBy(pageable: Pageable): Slice<ProductV3>
    fun findAllByAddedBy(addedBy: UserV3, pageable: Pageable): Slice<ProductV3>
}
