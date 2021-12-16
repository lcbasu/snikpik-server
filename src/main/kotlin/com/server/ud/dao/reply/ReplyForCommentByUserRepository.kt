package com.server.ud.dao.reply

import com.server.ud.entities.reply.ReplyForCommentByUser
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.cassandra.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ReplyForCommentByUserRepository : CassandraRepository<ReplyForCommentByUser?, String?> {
//    @Query("select * from reply_for_comment_by_user where comment_id = ?0 and user_id = ?1")
    fun findAllByCommentIdAndUserId(commentId: String, userId: String): List<ReplyForCommentByUser>
}
