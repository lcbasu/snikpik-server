package com.server.ud.entities

import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.Indexed
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.time.Instant

@Table("user_post")
class UserPost {

    @PrimaryKeyColumn(name = "user_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var userId: String? = null

    @PrimaryKeyColumn(name = "posted_at", ordinal = 1, ordering = Ordering.DESCENDING)
    var postedAt: Instant = Instant.now()

    @Indexed
    @PrimaryKeyColumn(name = "post_id", ordinal = 2)
    var postId: String? = null

    @Column
    var title: String? = null

    @Column
    var description: String? = null
}

