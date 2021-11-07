package com.server.ud.dao.user

import com.server.ud.entities.user.UsersByProfileCategory
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface UsersByProfileCategoryRepository : CassandraRepository<UsersByProfileCategory?, String?> {
//    @Query("select * from user_post where userId = ?0")
//    fun findByUserId(userId: String?): List<PostsByUser>
}
