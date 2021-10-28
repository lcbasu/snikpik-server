package com.server.ud.entities

import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.Indexed
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.time.Instant

@Table("post_comments")
class PostComment {

    @PrimaryKeyColumn(name = "comment_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var commentId: String? = null

    @PrimaryKeyColumn(name = "created_at", ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    var createdAt: Instant = Instant.now()

    @Indexed
    @PrimaryKeyColumn(name = "post_id", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
    var postId: String? = null

    @Indexed
    @PrimaryKeyColumn(name = "user_id", ordinal = 3, type = PrimaryKeyType.CLUSTERED)
    var userId: String? = null

    @Column
    var text: String? = null

    @Column
    var media: String? = null // MediaDetailsV2

}

