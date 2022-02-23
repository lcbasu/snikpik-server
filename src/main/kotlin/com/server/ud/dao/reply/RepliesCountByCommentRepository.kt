package com.server.ud.dao.reply

import com.server.ud.entities.reply.RepliesCountByComment
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.cassandra.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface RepliesCountByCommentRepository : CassandraRepository<RepliesCountByComment?, String?> {

//    @Query("select * from replies_count_by_comment where comment_id = ?0")
    fun findAllByCommentId(commentId: String?): List<RepliesCountByComment>

    @Query("UPDATE replies_count_by_comment SET replies_count = replies_count + 1 WHERE comment_id = ?0")
    fun incrementReplyCount(commentId: String)

    fun deleteByCommentId(commentId: String)
}
