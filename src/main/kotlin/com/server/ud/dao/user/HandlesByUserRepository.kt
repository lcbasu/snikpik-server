package com.server.ud.dao.user

import com.server.ud.entities.user.HandlesByUser
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface HandlesByUserRepository : CassandraRepository<HandlesByUser?, String?> {
//    @Query("select * from user_post where userId = ?0")
//    fun findByUserId(userId: String?): List<PostsByUser>
}
