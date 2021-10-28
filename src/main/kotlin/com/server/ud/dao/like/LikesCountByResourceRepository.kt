package com.server.ud.dao.like

import com.server.ud.entities.like.LikesCountByResource
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface LikesCountByResourceRepository : CassandraRepository<LikesCountByResource?, String?> {
//    @Query("select * from user_post where userId = ?0")
//    fun findByUserId(userId: String?): List<PostsByUser>
}
