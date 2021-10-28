package com.server.ud.dao.reply

import com.server.ud.entities.reply.RepliesByComment
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface RepliesByCommentRepository : CassandraRepository<RepliesByComment?, String?> {
//    @Query("select * from user_post where userId = ?0")
//    fun findByUserId(userId: String?): List<PostsByUser>
}
