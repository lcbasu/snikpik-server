package com.server.ud.dao.user_activity

import com.server.ud.entities.user_activity.UserActivityByUser
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Repository

@Repository
interface UserActivitiesByUserRepository : CassandraRepository<UserActivityByUser?, String?> {
    fun findAllByByUserId(byUserId: String, pageable: Pageable): Slice<UserActivityByUser>
}
