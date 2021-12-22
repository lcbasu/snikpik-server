package com.server.ud.entities.user_activity

import com.server.common.utils.DateUtils
import com.server.ud.enums.UserActivityType
import com.server.ud.enums.UserAggregateActivityType
import com.server.ud.model.*
import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.time.Instant

@Table("user_activities")
class UserActivity (

    @PrimaryKeyColumn(name = "user_activity_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var userActivityId: String,

    @PrimaryKeyColumn(name = "user_aggregate_activity_type", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    var userAggregateActivityType: UserAggregateActivityType,

    @PrimaryKeyColumn(name = "user_activity_type", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
    var userActivityType: UserActivityType,

    @PrimaryKeyColumn(name = "created_at", ordinal = 3, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    var createdAt: Instant = DateUtils.getInstantNow(),

    @Column("by_user_id")
    var byUserId: String,

    @Column("user_activity_data")
    var userActivityData: String, // String form of the data object

    @Column("for_user_id")
    var forUserId: String?,
)

fun UserActivity.getUserActivityData(): UserActivityData? {
    this.apply {
        return try {
            parseUserActivityData(userActivityData, userActivityType)
        } catch (e: Exception) {
            null
        }
    }
}

fun UserActivity.getUserActivityByUser(): UserActivityByUser? {
    this.apply {
        return try {
            return UserActivityByUser(
                userActivityId = userActivityId,
                userAggregateActivityType = userAggregateActivityType,
                userActivityType = userActivityType,
                createdAt = createdAt,
                byUserId = byUserId,
                userActivityData = userActivityData,
                forUserId = forUserId,
            )
        } catch (e: Exception) {
            null
        }
    }
}

fun UserActivity.getUserActivityForUser(): UserActivityForUser? {
    this.apply {
        return try {
            return forUserId?.let {
                UserActivityForUser(
                    userActivityId = userActivityId,
                    userAggregateActivityType = userAggregateActivityType,
                    userActivityType = userActivityType,
                    createdAt = createdAt,
                    byUserId = byUserId,
                    userActivityData = userActivityData,
                    forUserId = it,
                )
            }
        } catch (e: Exception) {
            null
        }
    }
}
