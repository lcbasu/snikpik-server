package com.server.ud.dao.comment

import com.server.ud.entities.comment.CommentsByPost
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.cassandra.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface CommentsByPostRepository : CassandraRepository<CommentsByPost?, String?> {
    @Query("select * from comments_by_post where post_id = ?0")
    fun findAllByPostId(postId: String?): List<CommentsByPost>
}
