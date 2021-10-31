package com.server.ud.dao.location

import com.server.ud.entities.location.LocationsByZipcode
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface LocationsByZipcodeRepository : CassandraRepository<LocationsByZipcode?, String?> {
//    @Query("select * from user_post where userId = ?0")
//    fun findByUserId(userId: String?): List<PostsByUser>
}
