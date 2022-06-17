package com.server.common.dao

import com.server.common.entities.UserIdByMobileNumber
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface UserIdByMobileNumberRepository : CassandraRepository<UserIdByMobileNumber?, String?> {
    fun findAllByAbsoluteMobile(absoluteMobileNumber: String): List<UserIdByMobileNumber>
}
