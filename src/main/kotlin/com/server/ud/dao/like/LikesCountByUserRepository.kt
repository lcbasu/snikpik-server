package com.server.ud.dao.like

import com.server.ud.entities.like.LikesCountByUser
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface LikesCountByUserRepository : CassandraRepository<LikesCountByUser?, String?> {
//    @Query("select * from user_post where userId = ?0")
//    fun findByUserId(userId: String?): List<PostsByUser>
}
