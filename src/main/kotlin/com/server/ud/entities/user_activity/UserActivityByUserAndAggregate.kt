package com.server.ud.entities.user_activity

import com.server.common.utils.DateUtils
import com.server.ud.enums.UserActivityType
import com.server.ud.enums.UserAggregateActivityType
import com.server.ud.model.UserActivityData
import com.server.ud.model.parseUserActivityData
import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.time.Instant

@Table("user_activities_by_user_and_aggregate")
class UserActivityByUserAndAggregate (

    @PrimaryKeyColumn(name = "by_user_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var byUserId: String,

    @PrimaryKeyColumn(name = "user_aggregate_activity_type", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
    var userAggregateActivityType: UserAggregateActivityType,

    @PrimaryKeyColumn(name = "created_at", ordinal = 2, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    var createdAt: Instant = DateUtils.getInstantNow(),

    @Column("user_activity_type")
    var userActivityType: UserActivityType,

    @Column("for_user_id")
    var forUserId: String?,

    @Column("user_activity_id")
    var userActivityId: String,

    @Column("user_activity_data")
    var userActivityData: String, // String form of the data object
)

fun UserActivityByUserAndAggregate.getUserActivityData(): UserActivityData? {
    this.apply {
        return try {
            parseUserActivityData(userActivityData, userActivityType)
        } catch (e: Exception) {
            null
        }
    }
}

fun UserActivityByUserAndAggregate.toUserActivityByUser(): UserActivityByUser {
    this.apply {
        return UserActivityByUser(
            userActivityId = userActivityId,
            userAggregateActivityType = userAggregateActivityType,
            userActivityType = userActivityType,
            createdAt = createdAt,
            byUserId = byUserId,
            userActivityData = userActivityData,
            forUserId = forUserId,
        )
    }
}
