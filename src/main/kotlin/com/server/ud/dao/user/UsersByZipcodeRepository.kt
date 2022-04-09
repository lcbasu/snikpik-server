package com.server.ud.dao.user

import com.server.ud.entities.user.UsersByZipcode
import com.server.ud.entities.user.UsersByZipcodeTracker
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
interface UsersByZipcodeRepository : CassandraRepository<UsersByZipcode?, String?> {
//    @AllowFiltering
//    fun findAllByUserId(userId: String): List<UsersByZipcode>

//    fun findAllByZipcodeAndCreatedAtAndUserId(zipcode: String, createdAt: Instant, userId: String): List<UsersByZipcode>

}

@Repository
interface UsersByZipcodeTrackerRepository : CassandraRepository<UsersByZipcodeTracker?, String?> {
    fun findAllByUserId(userId: String, pageable: Pageable): Slice<UsersByZipcodeTracker>
}
