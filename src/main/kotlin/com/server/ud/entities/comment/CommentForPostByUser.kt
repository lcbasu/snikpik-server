package com.server.ud.entities.comment

import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

/**
 *
 * This table store the data about whether a user commented on a post or not
 *
 * */
@Table("comment_for_post_by_user")
class CommentForPostByUser (

    @PrimaryKeyColumn(name = "post_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var postId: String,

    @PrimaryKeyColumn(name = "user_id", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
    var userId: String,

    var commented: Boolean = false
)

