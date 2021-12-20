package com.server.ud.dao.user

import com.server.ud.entities.user.UsersByProfileCategory
import org.springframework.data.cassandra.repository.AllowFiltering
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface UsersByProfileCategoryRepository : CassandraRepository<UsersByProfileCategory?, String?> {

    @AllowFiltering
    fun findAllByUserId(userId: String): List<UsersByProfileCategory>

}
