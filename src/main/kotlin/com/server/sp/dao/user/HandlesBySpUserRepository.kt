package com.server.sp.dao.user

import com.server.sp.entities.user.HandlesBySpUser
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface HandlesBySpUserRepository : CassandraRepository<HandlesBySpUser?, String?> {
//    @Query("select * from user_post where userId = ?0")
//    fun findByUserId(userId: String?): List<PostsByUser>
}
