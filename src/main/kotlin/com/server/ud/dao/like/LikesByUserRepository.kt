package com.server.ud.dao.like

import com.server.ud.entities.like.LikesByUser
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface LikesByUserRepository : CassandraRepository<LikesByUser?, String?> {
//    @Query("select * from user_post where userId = ?0")
//    fun findByUserId(userId: String?): List<PostsByUser>
}
