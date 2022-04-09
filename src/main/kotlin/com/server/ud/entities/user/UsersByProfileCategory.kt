package com.server.ud.entities.user

import com.server.common.enums.ProfileCategory
import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.time.Instant

@Table("users_by_profile_category")
class UsersByProfileCategory (

    @PrimaryKeyColumn(name = "profile_category", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var profileCategory: ProfileCategory,

//    @PrimaryKeyColumn(name = "for_date", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
//    var forDate: Instant,

    @PrimaryKeyColumn(name = "created_at", ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    var createdAt: Instant,

    @PrimaryKeyColumn(name = "user_id", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
    var userId: String,

    // Not storing any other data columns as they can be changed any time
    // but the above data points remains constant
)


@Table("users_by_profile_category_tracker")
class UsersByProfileCategoryTracker (

    @PrimaryKeyColumn(name = "user_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var userId: String,

    @PrimaryKeyColumn(name = "profile_category", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    var profileCategory: ProfileCategory,

//    @PrimaryKeyColumn(name = "for_date", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
//    var forDate: Instant,

    @PrimaryKeyColumn(name = "created_at", ordinal = 2, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    var createdAt: Instant,

    // Not storing any other data columns as they can be changed any time
    // but the above data points remains constant
)

fun UsersByProfileCategory.toUsersByProfileCategoryTracker(): UsersByProfileCategoryTracker {
    this.apply {
        return UsersByProfileCategoryTracker(
            userId = this.userId,
            profileCategory = this.profileCategory,
            createdAt = this.createdAt
        )
    }
}

fun UsersByProfileCategoryTracker.toUsersByProfileCategory(): UsersByProfileCategory {
    this.apply {
        return UsersByProfileCategory(
            userId = this.userId,
            profileCategory = this.profileCategory,
            createdAt = this.createdAt
        )
    }
}
