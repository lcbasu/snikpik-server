package com.server.ud.dao.post

import com.server.ud.entities.post.PostReportV2ByUser
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface PostReportByUserRepository : CassandraRepository<PostReportV2ByUser?, String?> {
    fun findAllByReportedByUserId(userId: String): List<PostReportV2ByUser>
}
