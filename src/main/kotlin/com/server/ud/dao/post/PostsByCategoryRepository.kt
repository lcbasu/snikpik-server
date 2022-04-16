package com.server.ud.dao.post

import com.server.ud.entities.post.PostsByCategory
import com.server.ud.entities.post.PostsByCategoryTracker
import com.server.ud.enums.CategoryV2
import com.server.ud.enums.PostType
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Repository

@Repository
interface PostsByCategoryRepository : CassandraRepository<PostsByCategory?, String?> {
    fun findAllByCategoryIdAndPostType(categoryV2: CategoryV2, postType: PostType, pageable: Pageable): Slice<PostsByCategory>
}

@Repository
interface PostsByCategoryTrackerRepository : CassandraRepository<PostsByCategoryTracker?, String?> {
    fun findAllByPostId(postId: String, pageable: Pageable): Slice<PostsByCategoryTracker>
}
