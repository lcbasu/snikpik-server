package com.server.ud.dao.post

import com.server.ud.entities.post.PostsByLocation
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface PostsByLocationRepository : CassandraRepository<PostsByLocation?, String?> {
//    @Query("select * from user_post where userId = ?0")
//    fun findByUserId(userId: String?): List<PostsByUser>
}
