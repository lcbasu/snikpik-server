package com.server.ud.dao.user

import com.server.ud.entities.user.UserReportV2ByUser
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface UserReportByUserRepository : CassandraRepository<UserReportV2ByUser?, String?> {
    fun findAllByReportedByUserId(userId: String): List<UserReportV2ByUser>
}
