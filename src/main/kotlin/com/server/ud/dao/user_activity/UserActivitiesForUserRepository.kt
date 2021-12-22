package com.server.ud.dao.user_activity

import com.server.ud.entities.user_activity.UserActivityForUser
import com.server.ud.enums.UserAggregateActivityType
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Repository

@Repository
interface UserActivitiesForUserRepository : CassandraRepository<UserActivityForUser?, String?> {
    fun findAllByForUserId(forUserId: String, pageable: Pageable): Slice<UserActivityForUser>
    fun findAllByForUserIdAndUserAggregateActivityType(forUserId: String, userAggregateActivityType: UserAggregateActivityType, pageable: Pageable): Slice<UserActivityForUser>
}
