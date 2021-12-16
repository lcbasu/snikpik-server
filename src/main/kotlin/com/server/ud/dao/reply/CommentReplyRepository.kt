package com.server.ud.dao.reply

import com.server.ud.entities.reply.Reply
import org.springframework.data.cassandra.repository.AllowFiltering
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface CommentReplyRepository : CassandraRepository<Reply?, String?> {

//    @Query("select * from replies where reply_id = ?0")
    fun findAllByReplyId(replyId: String?): List<Reply>

    @AllowFiltering
    fun findAllByPostId(postId: String): List<Reply>

}
