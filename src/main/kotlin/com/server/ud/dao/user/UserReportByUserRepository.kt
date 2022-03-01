package com.server.ud.dao.user

import com.server.ud.entities.user.UserReportByUser
import com.server.ud.entities.user.UserV2
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface UserReportByUserRepository : CassandraRepository<UserReportByUser?, String?> {
    fun findAllByReportedByUserId(userId: String): List<UserReportByUser>
}
