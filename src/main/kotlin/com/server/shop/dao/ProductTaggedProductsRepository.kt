package com.server.shop.dao

import com.server.shop.entities.PostTaggedProduct
import com.server.shop.entities.PostTaggedProductKey
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductTaggedProductsRepository : JpaRepository<PostTaggedProduct?, PostTaggedProductKey?> {
    fun findAllByPostIdOpen(postId: String, pageable: Pageable): Slice<PostTaggedProduct>
}
