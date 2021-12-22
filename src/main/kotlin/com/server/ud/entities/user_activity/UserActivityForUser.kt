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

@Table("user_activities_for_user")
class UserActivityForUser (

    @PrimaryKeyColumn(name = "for_user_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var forUserId: String,

    @PrimaryKeyColumn(name = "created_at", ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    var createdAt: Instant = DateUtils.getInstantNow(),

    @Column("user_aggregate_activity_type")
    var userAggregateActivityType: UserAggregateActivityType,

    @Column("user_activity_type")
    var userActivityType: UserActivityType,

    @Column("by_user_id")
    var byUserId: String,

    @Column("user_activity_id")
    var userActivityId: String,

    @Column("user_activity_data")
    var userActivityData: String, // String form of the data object
)

fun UserActivityForUser.getUserActivityData(): UserActivityData? {
    this.apply {
        return try {
            parseUserActivityData(userActivityData, userActivityType)
        } catch (e: Exception) {
            null
        }
    }
}

