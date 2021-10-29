package com.server.ud.dao.user

import com.server.ud.entities.user.UsersByZipcode
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface UsersByZipcodeRepository : CassandraRepository<UsersByZipcode?, String?> {
//    @Query("select * from user_post where userId = ?0")
//    fun findByUserId(userId: String?): List<PostsByUser>
}
