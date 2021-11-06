package com.server.ud.dao.reply

import com.server.ud.entities.reply.RepliesByComment
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.cassandra.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface RepliesByCommentRepository : CassandraRepository<RepliesByComment?, String?> {
    @Query("select * from replies_by_comment where comment_id = ?0")
    fun findAllByCommentId(commentId: String?): List<RepliesByComment>
}
