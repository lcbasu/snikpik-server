package com.server.ud.dao.user_activity

import com.server.ud.entities.user_activity.UserActivityByUserAndAggregate
import com.server.ud.enums.UserAggregateActivityType
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Repository

@Repository
interface UserActivitiesByUserAndAggregateRepository : CassandraRepository<UserActivityByUserAndAggregate?, String?> {
    fun findAllByByUserIdAndUserAggregateActivityType(byUserId: String, userAggregateActivityType: UserAggregateActivityType, pageable: Pageable): Slice<UserActivityByUserAndAggregate>
}
