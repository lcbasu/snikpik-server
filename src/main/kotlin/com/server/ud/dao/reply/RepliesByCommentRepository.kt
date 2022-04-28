package com.server.ud.dao.reply

import com.server.ud.entities.reply.RepliesByComment
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
interface RepliesByCommentRepository : CassandraRepository<RepliesByComment?, String?> {
    fun findAllByCommentId(commentId: String, pageable: Pageable): Slice<RepliesByComment>

    fun deleteAllByCommentIdAndCreatedAtAndReplyIdAndUserId(
        commentId: String,
        createdAt: Instant,
        replyId: String,
        userId: String
    )

//    @Query("select * from replies_by_comment where comment_id = ?0")
//    fun findAllByCommentId_V2(commentId: String): List<RepliesByComment>
}
