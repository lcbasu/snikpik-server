package com.server.ud.dao.save

import com.server.ud.entities.save.ResourceSave
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface ResourceSaveRepository : CassandraRepository<ResourceSave?, String?> {
//    @Query("select * from user_post where userId = ?0")
//    fun findByUserId(userId: String?): List<PostsByUser>
}
