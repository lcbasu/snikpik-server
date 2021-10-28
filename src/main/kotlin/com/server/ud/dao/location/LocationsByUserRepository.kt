package com.server.ud.dao.location

import com.server.ud.entities.location.LocationsByUser
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface LocationsByUserRepository : CassandraRepository<LocationsByUser?, String?> {
//    @Query("select * from user_post where userId = ?0")
//    fun findByUserId(userId: String?): List<PostsByUser>
}
