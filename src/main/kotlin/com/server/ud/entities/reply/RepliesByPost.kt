package com.server.ud.entities.reply

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.server.common.model.MediaDetailsV2
import com.server.common.model.getMediaDetailsFromJsonString
import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.time.Instant

@Table("replies_by_post")
class RepliesByPost (

    @PrimaryKeyColumn(name = "post_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var postId: String,

    @PrimaryKeyColumn(name = "comment_id", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    var commentId: String,

    @PrimaryKeyColumn(name = "created_at", ordinal = 2, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    var createdAt: Instant,

    @PrimaryKeyColumn(name = "reply_id", ordinal = 3, type = PrimaryKeyType.CLUSTERED)
    var replyId: String,

    @PrimaryKeyColumn(name = "user_id", ordinal = 4, type = PrimaryKeyType.CLUSTERED)
    var userId: String,

    @Column("reply_text")
    var replyText: String,

    @Column
    var media: String? = null, // MediaDetailsV2
)
