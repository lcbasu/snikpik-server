package com.server.ud.dao.location

import com.server.ud.entities.location.NearbyZipcodesByZipcode
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface NearbyZipcodesByZipcodeRepository : CassandraRepository<NearbyZipcodesByZipcode?, String?> {
//    @Query("select * from user_post where userId = ?0")
//    fun findByUserId(userId: String?): List<PostsByUser>
}
