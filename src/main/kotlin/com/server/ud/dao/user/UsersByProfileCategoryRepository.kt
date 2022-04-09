package com.server.ud.dao.user

import com.server.common.enums.ProfileCategory
import com.server.ud.entities.user.UsersByProfileCategory
import com.server.ud.entities.user.UsersByProfileCategoryTracker
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
interface UsersByProfileCategoryRepository : CassandraRepository<UsersByProfileCategory?, String?> {

//    @AllowFiltering
//    fun findAllByUserId(userId: String): List<UsersByProfileCategory>

//    fun findAllByProfileCategoryAndCreatedAtAndUserId(profileCategory: ProfileCategory, createdAt: Instant, userId: String): List<UsersByProfileCategory>


}

@Repository
interface UsersByProfileCategoryTrackerRepository : CassandraRepository<UsersByProfileCategoryTracker?, String?> {
    fun findAllByUserId(userId: String, pageable: Pageable): Slice<UsersByProfileCategoryTracker>
}
