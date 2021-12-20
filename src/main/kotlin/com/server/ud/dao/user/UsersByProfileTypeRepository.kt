package com.server.ud.dao.user

import com.server.ud.entities.user.UsersByProfileType
import org.springframework.data.cassandra.repository.AllowFiltering
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface UsersByProfileTypeRepository : CassandraRepository<UsersByProfileType?, String?> {
    @AllowFiltering
    fun findAllByUserId(userId: String): List<UsersByProfileType>
}
