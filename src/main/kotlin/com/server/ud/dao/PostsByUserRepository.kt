package com.server.ud.dao

import com.server.ud.entities.PostsByUser
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.cassandra.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface PostsByUserRepository : CassandraRepository<PostsByUser?, String?> {
//    @Query("select * from user_post where userId = ?0")
//    fun findByUserId(userId: String?): List<PostsByUser>
}
