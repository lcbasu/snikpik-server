package com.server.ud.dao.reply

import com.server.ud.entities.reply.RepliesByComment
import org.springframework.data.cassandra.repository.AllowFiltering
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Repository

@Repository
interface RepliesByCommentRepository : CassandraRepository<RepliesByComment?, String?> {
    fun findAllByCommentId(commentId: String, pageable: Pageable): Slice<RepliesByComment>

    @AllowFiltering
    fun findAllByPostId(postId: String): List<RepliesByComment>
}
