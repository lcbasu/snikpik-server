package com.server.ud.dao.user

import com.server.ud.entities.user.UserV2ByMobileNumber
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface UserV2ByMobileNumberRepository : CassandraRepository<UserV2ByMobileNumber?, String?> {
    fun findAllByAbsoluteMobile(absoluteMobileNumber: String): List<UserV2ByMobileNumber>
}
