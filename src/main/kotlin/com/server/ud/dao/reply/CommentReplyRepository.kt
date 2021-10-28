package com.server.ud.dao.reply

import com.server.ud.entities.reply.CommentReply
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface CommentReplyRepository : CassandraRepository<CommentReply?, String?> {
//    @Query("select * from user_post where userId = ?0")
//    fun findByUserId(userId: String?): List<PostsByUser>
}
