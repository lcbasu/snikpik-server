package com.server.ud.dao.social

import com.server.ud.entities.social.Following
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface FollowingRepository : CassandraRepository<Following?, String?> {
//    @Query("select * from user_post where userId = ?0")
//    fun findByUserId(userId: String?): List<PostsByUser>
}
