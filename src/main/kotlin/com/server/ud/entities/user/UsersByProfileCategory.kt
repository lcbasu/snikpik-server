package com.server.ud.entities.user

import com.server.common.enums.ProfileCategory
import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.time.Instant

@Table("users_by_profile_category")
class UsersByProfileCategory (

    @PrimaryKeyColumn(name = "profile_category", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var profileCategory: ProfileCategory,

    @PrimaryKeyColumn(name = "for_date", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
    var forDate: Instant,

    @PrimaryKeyColumn(name = "created_at", ordinal = 2, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    var createdAt: Instant,

    @PrimaryKeyColumn(name = "user_id", ordinal = 3, type = PrimaryKeyType.CLUSTERED)
    var userId: String,

    // Not storing any other data columns as they can be changed any time
    // but the above data points remains constant
)

