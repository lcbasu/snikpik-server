package com.server.ud.entities.user

import com.server.common.enums.ProfileType
import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.time.Instant

@Table("users_by_profile_type")
class UsersByProfileType (

    @PrimaryKeyColumn(name = "profile_type", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var profileType: ProfileType,

//    @PrimaryKeyColumn(name = "for_date", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
//    var forDate: Instant,

    @PrimaryKeyColumn(name = "created_at", ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    var createdAt: Instant,

    @PrimaryKeyColumn(name = "user_id", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
    var userId: String,

)


@Table("users_by_profile_type_tracker")
class UsersByProfileTypeTracker (

    @PrimaryKeyColumn(name = "user_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var userId: String,

    @PrimaryKeyColumn(name = "profile_type", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    var profileType: ProfileType,

    @PrimaryKeyColumn(name = "created_at", ordinal = 2, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    var createdAt: Instant,

)

fun UsersByProfileType.toUsersByProfileTypeTracker(): UsersByProfileTypeTracker {
    this.apply {
        return UsersByProfileTypeTracker(
            userId = this.userId,
            profileType = this.profileType,
            createdAt = this.createdAt
        )
    }
}


fun UsersByProfileTypeTracker.toUsersByProfileType(): UsersByProfileType {
    this.apply {
        return UsersByProfileType(
            userId = this.userId,
            profileType = this.profileType,
            createdAt = this.createdAt
        )
    }
}

