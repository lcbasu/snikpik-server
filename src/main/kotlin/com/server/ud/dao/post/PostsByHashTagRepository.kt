package com.server.ud.dao.post

import com.server.ud.entities.post.PostsByCategory
import com.server.ud.entities.post.PostsByHashTag
import com.server.ud.entities.post.PostsByZipcode
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface PostsByHashTagRepository : CassandraRepository<PostsByHashTag?, String?> {
//    @Query("select * from user_post where userId = ?0")
//    fun findByUserId(userId: String?): List<PostsByUser>
}
