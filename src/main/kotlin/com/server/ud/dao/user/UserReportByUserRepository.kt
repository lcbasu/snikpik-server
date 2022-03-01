package com.server.ud.dao.user

import com.server.ud.entities.user.UserReportV2ByUser
import com.server.ud.enums.UserReportActionType
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface UserReportByUserRepository : CassandraRepository<UserReportV2ByUser?, String?> {
    fun findAllByReportedByUserId(userId: String): List<UserReportV2ByUser>
    fun deleteByReportedByUserIdAndActionAndReportedForUserId(reportedByUserId: String, action: UserReportActionType, reportedForUserId: String)
}
