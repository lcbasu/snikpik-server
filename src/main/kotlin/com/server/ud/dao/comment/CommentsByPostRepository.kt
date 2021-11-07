package com.server.ud.dao.comment

import com.server.ud.entities.comment.CommentsByPost
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Repository

@Repository
interface CommentsByPostRepository : CassandraRepository<CommentsByPost?, String?> {
    fun findAllByPostId(postId: String, pageable: Pageable): Slice<CommentsByPost>
}
