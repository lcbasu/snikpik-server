package com.server.ud.entities.social

import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.Indexed
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.time.Instant

@Table("followers")
class Follower {

    @PrimaryKeyColumn(name = "user_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var userId: String? = null

    @PrimaryKeyColumn(name = "created_at", ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    var createdAt: Instant = Instant.now()

    @PrimaryKeyColumn(name = "user_handle", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
    val userHandle: String? = null

    @PrimaryKeyColumn(name = "follower_user_id", ordinal = 3, type = PrimaryKeyType.CLUSTERED)
    var followerUserId: String? = null

    @PrimaryKeyColumn(name = "follower_handle", ordinal = 4, type = PrimaryKeyType.CLUSTERED)
    var followerHandle: String? = null

    @Column("user_full_name")
    val userFullName: String? = null

    @Column("follower_full_name")
    val followerFullName: String? = null
}

