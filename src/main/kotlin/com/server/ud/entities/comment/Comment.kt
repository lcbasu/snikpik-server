package com.server.ud.entities.comment

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.server.common.utils.DateUtils
import com.server.common.model.MediaDetailsV2
import com.server.common.model.getMediaDetailsFromJsonString
import com.server.ud.enums.PostType
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.CassandraType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.time.Instant

@Table("comments")
class Comment (

    @PrimaryKeyColumn(name = "comment_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var commentId: String,

    @Column("created_at")
    @CassandraType(type = CassandraType.Name.TIMESTAMP)
    var createdAt: Instant = DateUtils.getInstantNow(),

    @Column("post_id")
    var postId: String,

    @Column("user_id")
    var userId: String,

    @Column("post_type")
    var postType: PostType,

    @Column
    var text: String,

    @Column
    var media: String? = null // MediaDetailsV2
)

fun Comment.getMediaDetails(): MediaDetailsV2? {
    this.apply {
        return getMediaDetailsFromJsonString(media)
    }
}
