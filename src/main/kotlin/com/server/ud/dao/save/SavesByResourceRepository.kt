package com.server.ud.dao.save

import com.server.ud.entities.save.SavesByResource
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface SavesByResourceRepository : CassandraRepository<SavesByResource?, String?> {
//    @Query("select * from user_post where userId = ?0")
//    fun findByUserId(userId: String?): List<PostsByUser>
}
