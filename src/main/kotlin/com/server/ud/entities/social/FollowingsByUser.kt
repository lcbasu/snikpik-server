package com.server.ud.entities.social

import com.server.common.utils.DateUtils
import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.time.Instant

@Table("followings_by_user")
class FollowingsByUser (

    @PrimaryKeyColumn(name = "user_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var userId: String,

    @PrimaryKeyColumn(name = "for_date", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    var forDate: Instant = DateUtils.getInstantToday(),

    @PrimaryKeyColumn(name = "created_at", ordinal = 2, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    var createdAt: Instant = DateUtils.getInstantNow(),

    @PrimaryKeyColumn(name = "following_user_id", ordinal = 3, type = PrimaryKeyType.CLUSTERED)
    var followingUserId: String,

    @Column("user_handle")
    val userHandle: String? = null,

    @Column("following_handle")
    var followingHandle: String? = null,

    @Column("user_full_name")
    val userFullName: String? = null,

    @Column("following_full_name")
    val followingFullName: String? = null,
)

