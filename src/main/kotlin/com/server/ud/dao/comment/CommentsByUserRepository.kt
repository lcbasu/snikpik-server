package com.server.ud.dao.comment

import com.server.ud.entities.comment.CommentsByUser
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface CommentsByUserRepository : CassandraRepository<CommentsByUser?, String?> {
//    @Query("select * from user_post where userId = ?0")
//    fun findByUserId(userId: String?): List<PostsByUser>
}
