package com.server.ud.entities.comment

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.server.dk.model.MediaDetailsV2
import com.server.ud.enums.PostType
import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.time.Instant

@Table("comments_by_post")
class CommentsByPost (

    @PrimaryKeyColumn(name = "post_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var postId: String,

    @PrimaryKeyColumn(name = "created_at", ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    var createdAt: Instant,

    @PrimaryKeyColumn(name = "comment_id", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
    var commentId: String,

    @PrimaryKeyColumn(name = "user_id", ordinal = 3, type = PrimaryKeyType.CLUSTERED)
    var userId: String,

    @PrimaryKeyColumn(name = "post_type", ordinal = 4, type = PrimaryKeyType.CLUSTERED)
    var postType: PostType,

    @Column
    var text: String,

    @Column
    var media: String? = null // MediaDetailsV2
)

fun CommentsByPost.getMediaDetails(): MediaDetailsV2? {
    this.apply {
        return try {
            jacksonObjectMapper().readValue(media, MediaDetailsV2::class.java)
        } catch (e: Exception) {
            MediaDetailsV2(emptyList())
        }
    }
}
