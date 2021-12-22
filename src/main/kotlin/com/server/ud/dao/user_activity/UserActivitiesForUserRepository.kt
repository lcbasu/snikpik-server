package com.server.ud.dao.user_activity

import com.server.ud.entities.user_activity.UserActivityForUser
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Repository

@Repository
interface UserActivitiesForUserRepository : CassandraRepository<UserActivityForUser?, String?> {
    fun findAllByForUserId(forUserId: String, pageable: Pageable): Slice<UserActivityForUser>
}
