package com.server.ud.dao.comment

import com.server.ud.entities.comment.CommentsByUser
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.cassandra.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface CommentsByUserRepository : CassandraRepository<CommentsByUser?, String?> {
    @Query("select * from comments_by_user where user_id = ?0")
    fun findAllByUserId(userId: String?): List<CommentsByUser>
}
