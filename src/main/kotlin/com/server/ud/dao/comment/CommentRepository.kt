package com.server.ud.dao.comment

import com.server.ud.entities.comment.Comment
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.cassandra.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface CommentRepository : CassandraRepository<Comment?, String?> {
    @Query("select * from comments where comment_id = ?0")
    fun findAllByCommentId(likeId: String?): List<Comment>
}
