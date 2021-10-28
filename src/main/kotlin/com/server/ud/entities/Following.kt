package com.server.ud.entities

import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.Indexed
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.time.Instant

@Table("followings")
class Following {

    @PrimaryKeyColumn(name = "user_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var userId: String? = null

    @PrimaryKeyColumn(name = "created_at", ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    var createdAt: Instant = Instant.now()

    @Indexed
    @PrimaryKeyColumn(name = "user_handle", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
    val userHandle: String? = null

    @Indexed
    @PrimaryKeyColumn(name = "followed_user_id", ordinal = 3, type = PrimaryKeyType.CLUSTERED)
    var followedUserId: String? = null

    @Indexed
    @PrimaryKeyColumn(name = "followed_handle", ordinal = 4, type = PrimaryKeyType.CLUSTERED)
    var followedHandle: String? = null

    @Column("user_full_name")
    val userFullName: String? = null

    @Column("followed_full_name")
    val followedFullName: String? = null
}

