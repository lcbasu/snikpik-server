package com.server.ud.entities.user

import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.time.Instant

@Table("users_by_zipcode")
class UsersByZipcode (

    @PrimaryKeyColumn(name = "zipcode", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var zipcode: String,

//    @PrimaryKeyColumn(name = "for_date", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
//    var forDate: Instant,

    @PrimaryKeyColumn(name = "created_at", ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    var createdAt: Instant,

    @PrimaryKeyColumn(name = "user_id", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
    var userId: String,

)

@Table("users_by_zipcode_tracker")
class UsersByZipcodeTracker (

    @PrimaryKeyColumn(name = "user_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var userId: String,

    @PrimaryKeyColumn(name = "zipcode", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    var zipcode: String,

//    @PrimaryKeyColumn(name = "for_date", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
//    var forDate: Instant,

    @PrimaryKeyColumn(name = "created_at", ordinal = 2, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    var createdAt: Instant,

)


fun UsersByZipcode.toUsersByZipcodeTracker(): UsersByZipcodeTracker {
    this.apply {
        return UsersByZipcodeTracker(
            userId = this.userId,
            zipcode = this.zipcode,
            createdAt = this.createdAt,
        )
    }
}


fun UsersByZipcodeTracker.toUsersByZipcode(): UsersByZipcode {
    this.apply {
        return UsersByZipcode(
            userId = this.userId,
            zipcode = this.zipcode,
            createdAt = this.createdAt,
        )
    }
}
