package com.server.ud.dao.comment

import com.server.ud.entities.comment.CommentsByPost
import org.springframework.data.cassandra.repository.AllowFiltering
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.cassandra.repository.Query
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Repository

@Repository
interface CommentsByPostRepository : CassandraRepository<CommentsByPost?, String?> {
    fun findAllByPostId(postId: String, pageable: Pageable): Slice<CommentsByPost>

    @AllowFiltering
    @Query("select * from comments_by_post where post_id = ?0")
    fun findAllByPostId_V2(postId: String): List<CommentsByPost>
}
