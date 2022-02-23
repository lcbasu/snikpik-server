package com.server.ud.dao.reply

import com.server.ud.entities.reply.RepliesByPost
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface RepliesByPostRepository : CassandraRepository<RepliesByPost?, String?> {
    fun findAllByPostId(postId: String): List<RepliesByPost>
}
