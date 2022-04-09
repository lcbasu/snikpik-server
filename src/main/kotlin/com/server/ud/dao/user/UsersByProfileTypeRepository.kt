package com.server.ud.dao.user

import com.server.common.enums.ProfileType
import com.server.ud.entities.user.UsersByProfileType
import com.server.ud.entities.user.UsersByProfileTypeTracker
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
interface UsersByProfileTypeRepository : CassandraRepository<UsersByProfileType?, String?> {
//    @AllowFiltering
//    fun findAllByUserId(userId: String): List<UsersByProfileType>

//    fun findAllByProfileTypeAndCreatedAtAndUserId(profileType: ProfileType, createdAt: Instant, userId: String): List<UsersByProfileType>

}

@Repository
interface UsersByProfileTypeTrackerRepository : CassandraRepository<UsersByProfileTypeTracker?, String?> {
    fun findAllByUserId(userId: String, pageable: Pageable): Slice<UsersByProfileTypeTracker>
}
