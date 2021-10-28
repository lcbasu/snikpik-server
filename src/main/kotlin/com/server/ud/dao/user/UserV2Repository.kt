package com.server.ud.dao.user

import com.server.ud.entities.user.UserV2
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface UserV2Repository : CassandraRepository<UserV2?, String?> {
//    @Query("select * from user_post where userId = ?0")
//    fun findByUserId(userId: String?): List<PostsByUser>
}
