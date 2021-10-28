package com.server.ud.dao

import com.server.ud.entities.Post
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface PostRepository : CassandraRepository<Post?, String?> {
//    @Query("select * from user_post where userId = ?0")
//    fun findByUserId(userId: String?): List<PostsByUser>
}
