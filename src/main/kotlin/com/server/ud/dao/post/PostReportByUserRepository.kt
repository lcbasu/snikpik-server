package com.server.ud.dao.post

import com.server.ud.entities.post.PostReportByUser
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface PostReportByUserRepository : CassandraRepository<PostReportByUser?, String?> {
    fun findAllByReportedByUserId(userId: String): List<PostReportByUser>
}
