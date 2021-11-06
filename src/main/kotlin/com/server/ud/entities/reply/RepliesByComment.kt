package com.server.ud.entities.reply

import com.server.common.utils.DateUtils
import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.Indexed
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.time.Instant

@Table("replies_by_comment")
class RepliesByComment (

    @PrimaryKeyColumn(name = "comment_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var commentId: String,

    @PrimaryKeyColumn(name = "created_at", ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    var createdAt: Instant,

    @PrimaryKeyColumn(name = "reply_id", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
    var replyId: String,

    @PrimaryKeyColumn(name = "user_id", ordinal = 3, type = PrimaryKeyType.CLUSTERED)
    var userId: String,

    @Column("reply_text")
    var replyText: String,

    @Column
    var media: String? = null, // MediaDetailsV2
)

