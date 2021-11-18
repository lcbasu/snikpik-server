package com.server.ud.entities.social

import com.server.common.utils.DateUtils
import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.time.Instant

@Table("followers_by_user")
class FollowersByUser (

    @PrimaryKeyColumn(name = "user_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var userId: String,

    @PrimaryKeyColumn(name = "for_date", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
    var forDate: Instant = DateUtils.getInstantToday(),

    @PrimaryKeyColumn(name = "created_at", ordinal = 2, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    var createdAt: Instant = DateUtils.getInstantNow(),

    @PrimaryKeyColumn(name = "follower_user_id", ordinal = 3, type = PrimaryKeyType.CLUSTERED)
    var followerUserId: String,

    @Column("user_handle")
    val userHandle: String? = null,

    @Column("follower_handle")
    var followerHandle: String? = null,

    @Column("user_full_name")
    val userFullName: String? = null,

    @Column("follower_full_name")
    val followerFullName: String? = null,
)

