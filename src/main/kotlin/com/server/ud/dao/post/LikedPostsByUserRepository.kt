package com.server.ud.dao.post

import com.server.ud.entities.post.LikedPostsByUser
import com.server.ud.enums.PostType
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Repository

@Repository
interface LikedPostsByUserRepository : CassandraRepository<LikedPostsByUser?, String?> {
    fun findAllByUserIdAndPostTypeAndLiked(userId: String, postType: PostType, liked: Boolean, pageable: Pageable): Slice<LikedPostsByUser>
}
