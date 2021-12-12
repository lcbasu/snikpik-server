package com.server.ud.dao.user

import com.server.common.enums.ProfileCategory
import com.server.ud.entities.user.ProfileTypesByNearbyZipcode
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Repository

@Repository
interface ProfileTypesByNearbyZipcodeAndProfileCategoryRepository : CassandraRepository<ProfileTypesByNearbyZipcode?, String?> {
    fun findAllByZipcodeAndProfileCategory(zipcode: String, profileCategory: ProfileCategory, pageable: Pageable): Slice<ProfileTypesByNearbyZipcode>
}
