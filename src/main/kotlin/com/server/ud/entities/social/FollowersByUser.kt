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

    // Not partitioning on date as it ias hard to get all followers by date
    // as we do not know when they started following
    // and the most prominent use case of this api
    // is during saving post for followers which is a very frequent use case
    // So we can not just allow for_date partition and
    // @AllowFilter on query, that will be equally bad
    // Keeping for_date as cluster so that we can query based on date if we want to
//    @PrimaryKeyColumn(name = "for_date", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
//    var forDate: Instant = DateUtils.getInstantToday(),

    @PrimaryKeyColumn(name = "created_at", ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    var createdAt: Instant = DateUtils.getInstantNow(),

    @PrimaryKeyColumn(name = "follower_user_id", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
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

