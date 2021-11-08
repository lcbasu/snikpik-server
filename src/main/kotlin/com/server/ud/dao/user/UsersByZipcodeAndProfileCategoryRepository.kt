package com.server.ud.dao.user

import com.server.common.enums.ProfileCategory
import com.server.ud.entities.user.UsersByZipcodeAndProfileCategory
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Repository

@Repository
interface UsersByZipcodeAndProfileCategoryRepository : CassandraRepository<UsersByZipcodeAndProfileCategory?, String?> {
    fun findAllByZipcodeAndProfileCategory(zipcode: String, profileCategory: ProfileCategory, pageable: Pageable): Slice<UsersByZipcodeAndProfileCategory>
}
