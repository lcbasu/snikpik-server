package com.server.ud.entities.social

import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.Indexed
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.time.Instant

// No need to add userId as same tag can be created by any user
// We just need to store how many posts are there with a tag
// and all the posts that are there in a tag
@Table("hash_tags")
class HashTag {

    // hashTagId and displayName should be same for all HashTag
    @PrimaryKeyColumn(name = "hash_tag_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var hashTagId: String? = null

    @PrimaryKeyColumn(name = "created_at", ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    var createdAt: Instant = Instant.now()

    // User who created this hashtag
    @PrimaryKeyColumn(name = "user_id", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
    var userId: String? = null

    @Column
    var displayName: String? = null

    @Column
    var firstPostId: String? = null
}

