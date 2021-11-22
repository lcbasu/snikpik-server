package com.server.ud.entities.social

import com.server.common.utils.DateUtils
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.CassandraType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.time.Instant

// No need to add userId as same tag can be created by any user
// We just need to store how many posts are there with a tag
// and all the posts that are there in a tag
@Table("hash_tags")
class HashTag (

    // hashTagId and displayName should be same for all HashTag
    @PrimaryKeyColumn(name = "hash_tag_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var hashTagId: String,

    @Column("created_at")
    @CassandraType(type = CassandraType.Name.TIMESTAMP)
    var createdAt: Instant = DateUtils.getInstantNow(),

    // User who created this hashtag
    @Column("user_id")
    var userId: String,

    @Column
    var displayName: String,

    @Column
    var firstPostId: String? = null,
)

