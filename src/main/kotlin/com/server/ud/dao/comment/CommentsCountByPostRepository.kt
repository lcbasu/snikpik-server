package com.server.ud.dao.comment

import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface CommentsCountByPostRepository : CassandraRepository<CommentsCountByPostRepository?, String?> {
//    @Query("select * from user_post where userId = ?0")
//    fun findByUserId(userId: String?): List<PostsByUser>
}
