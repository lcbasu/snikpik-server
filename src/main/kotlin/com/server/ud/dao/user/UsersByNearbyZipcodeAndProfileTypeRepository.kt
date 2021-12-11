package com.server.ud.dao.user

import com.server.common.enums.ProfileType
import com.server.ud.entities.user.UsersByNearbyZipcodeAndProfileType
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Repository

@Repository
interface UsersByNearbyZipcodeAndProfileTypeRepository : CassandraRepository<UsersByNearbyZipcodeAndProfileType?, String?> {
    fun findAllByZipcodeAndProfileType(zipcode: String, profileType: ProfileType, pageable: Pageable): Slice<UsersByNearbyZipcodeAndProfileType>
}
