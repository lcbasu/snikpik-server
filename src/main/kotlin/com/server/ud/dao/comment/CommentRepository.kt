package com.server.ud.dao.comment

import com.server.ud.entities.comment.Comment
import org.springframework.data.cassandra.repository.AllowFiltering
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface CommentRepository : CassandraRepository<Comment?, String?> {
//    @Query("select * from comments where comment_id = ?0")
    fun findAllByCommentId(commentId: String?): List<Comment>

    @AllowFiltering
    fun findAllByPostId(postId: String): List<Comment>
}
