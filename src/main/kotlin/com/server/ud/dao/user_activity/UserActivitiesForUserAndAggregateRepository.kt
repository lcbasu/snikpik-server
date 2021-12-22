package com.server.ud.dao.user_activity

import com.server.ud.entities.user_activity.UserActivityForUserAndAggregate
import com.server.ud.enums.UserAggregateActivityType
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Repository

@Repository
interface UserActivitiesForUserAndAggregateRepository : CassandraRepository<UserActivityForUserAndAggregate?, String?> {
    fun findAllByForUserIdAndUserAggregateActivityType(forUserId: String, userAggregateActivityType: UserAggregateActivityType, pageable: Pageable): Slice<UserActivityForUserAndAggregate>
}
