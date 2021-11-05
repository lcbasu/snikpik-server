package com.server.ud.dao.post

import com.server.ud.entities.post.PostsByCategory
import com.server.ud.enums.CategoryV2
import org.springframework.data.cassandra.repository.AllowFiltering
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
interface PostsByCategoryRepository : CassandraRepository<PostsByCategory?, String?> {
//    @AllowFiltering
//    fun findAllByCategoryId(categoryV2: CategoryV2, pageable: Pageable): Slice<PostsByCategory>

    fun findAllByCategoryIdAndForDate(categoryV2: CategoryV2, forDate: Instant, pageable: Pageable): Slice<PostsByCategory>
}
