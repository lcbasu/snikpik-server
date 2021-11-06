package com.server.ud.entities.reply

import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

/**
 *
 * This table store the data about whether a user replied on a comment or not
 *
 * */
@Table("reply_for_comment_by_user")
class ReplyForCommentByUser (

    @PrimaryKeyColumn(name = "comment_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var commentId: String,

    @PrimaryKeyColumn(name = "user_id", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
    var userId: String,

    var replied: Boolean = false
)

