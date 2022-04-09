package com.server.ud.dao.user

import com.server.common.enums.ProfileType
import com.server.ud.entities.user.UsersByNearbyZipcodeAndProfileType
import com.server.ud.entities.user.UsersByNearbyZipcodeAndProfileTypeTracker
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Repository

@Repository
interface UsersByNearbyZipcodeAndProfileTypeRepository : CassandraRepository<UsersByNearbyZipcodeAndProfileType?, String?> {
    fun findAllByZipcodeAndProfileType(zipcode: String, profileType: ProfileType, pageable: Pageable): Slice<UsersByNearbyZipcodeAndProfileType>

//    fun findAllByZipcodeAndProfileTypeAndUserId(zipcode: String, profileType: ProfileType, userId: String): List<UsersByNearbyZipcodeAndProfileType>
}

@Repository
interface UsersByNearbyZipcodeAndProfileTypeTrackerRepository : CassandraRepository<UsersByNearbyZipcodeAndProfileTypeTracker?, String?> {
    fun findAllByUserId(userId: String, pageable: Pageable): Slice<UsersByNearbyZipcodeAndProfileTypeTracker>
}
