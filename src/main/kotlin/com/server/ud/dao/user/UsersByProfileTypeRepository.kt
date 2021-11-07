package com.server.ud.dao.user

import com.server.ud.entities.user.UsersByProfileType
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface UsersByProfileTypeRepository : CassandraRepository<UsersByProfileType?, String?> {
//    @Query("select * from user_post where userId = ?0")
//    fun findByUserId(userId: String?): List<PostsByUser>
}
