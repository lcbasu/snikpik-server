package com.server.ud.dao.user

import com.server.common.enums.ProfileType
import com.server.ud.entities.user.UsersByZipcodeAndProfileType
import com.server.ud.entities.user.UsersByZipcodeAndProfileTypeTracker
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Repository

@Repository
interface UsersByZipcodeAndProfileTypeRepository : CassandraRepository<UsersByZipcodeAndProfileType?, String?> {
    fun findAllByZipcodeAndProfileType(zipcode: String, profileType: ProfileType, pageable: Pageable): Slice<UsersByZipcodeAndProfileType>

//    fun findAllByZipcodeAndProfileTypeAndUserId(zipcode: String, profileType: ProfileType, userId: String): List<UsersByZipcodeAndProfileType>
}

@Repository
interface UsersByZipcodeAndProfileTypeTrackerRepository : CassandraRepository<UsersByZipcodeAndProfileTypeTracker?, String?> {
    fun findAllByUserId(userId: String, pageable: Pageable): Slice<UsersByZipcodeAndProfileTypeTracker>
}
