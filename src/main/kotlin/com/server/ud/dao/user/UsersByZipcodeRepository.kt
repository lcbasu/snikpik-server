package com.server.ud.dao.user

import com.server.ud.entities.user.UsersByZipcode
import org.springframework.data.cassandra.repository.AllowFiltering
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface UsersByZipcodeRepository : CassandraRepository<UsersByZipcode?, String?> {
    @AllowFiltering
    fun findAllByUserId(userId: String): List<UsersByZipcode>
}
