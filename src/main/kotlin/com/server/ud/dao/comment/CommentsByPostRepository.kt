package com.server.ud.dao.comment

import com.server.ud.entities.comment.CommentsByPost
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface CommentsByPostRepository : CassandraRepository<CommentsByPost?, String?> {
//    @Query("select * from user_post where userId = ?0")
//    fun findByUserId(userId: String?): List<PostsByUser>
}
