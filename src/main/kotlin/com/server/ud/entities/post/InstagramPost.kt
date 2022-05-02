package com.server.ud.entities.post

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.server.common.model.MediaDetailsV2
import com.server.common.model.SingleMediaDetail
import com.server.common.model.getMediaDetailsFromJsonString
import com.server.common.utils.DateUtils
import com.server.ud.dto.InstagramPostChildrenResponse
import com.server.ud.enums.InstagramMediaType
import com.server.ud.enums.InstagramPostProcessingState
import com.server.ud.enums.getMediaType
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.CassandraType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.time.Instant

@Table("instagram_posts")
data class InstagramPost (

    @PrimaryKeyColumn(name = "user_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    val userId: String,

    // Id of the account
    @PrimaryKeyColumn(name = "account_id", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    val accountId: String,

    @PrimaryKeyColumn(name = "post_id", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
    val postId: String,

    @Column("unbox_post_id")
    var unboxPostId: String? = "",

    @Column("media_type")
    var mediaType: InstagramMediaType,

    @Column("state")
    var state: InstagramPostProcessingState,

    @Column("created_at")
    @CassandraType(type = CassandraType.Name.TIMESTAMP)
    val createdAt: Instant = DateUtils.getInstantNow(),

    @Column("caption")
    var caption: String? = null,

    // In case of carousel media, this is the first media in the carousel
    @Column("media_url")
    var mediaUrl: String,

    @Column("thumbnail_url")
    var thumbnailUrl: String? = null,

    @Column("permalink")
    var permalink: String? = null,

    @Column("timestamp")
    var timestamp: String? = null,

    @Column("username")
    var username: String? = null,

    // In case of carousel media, this is the string version of InstagramPostChildrenResponse
    @Column("children")
    var children: String? = null, // String of InstagramPostChildrenResponse
)

fun InstagramPost.getInstagramPostChildrenResponse(): InstagramPostChildrenResponse {
    this.apply {
        return try {
            jacksonObjectMapper().readValue(children, InstagramPostChildrenResponse::class.java)
        } catch (e: Exception) {
            InstagramPostChildrenResponse(emptyList())
        }
    }
}


fun InstagramPost.getMediaDetails(): MediaDetailsV2 {
    this.apply {
        val children = getInstagramPostChildrenResponse()
        if (children.data.isEmpty()) {
            return MediaDetailsV2(media = listOf(
                SingleMediaDetail(
                    mediaUrl = mediaUrl,
                    thumbnailUrl = if (thumbnailUrl == null) mediaUrl else thumbnailUrl,
                    mediaType = mediaType.getMediaType(),
                )
            ))
        } else {
            return MediaDetailsV2(
                media = children.data.map {
                    SingleMediaDetail(
                        mediaUrl = it.mediaUrl,
                        thumbnailUrl = if (it.thumbnailUrl == null) it.mediaUrl else it.thumbnailUrl,
                        mediaType = it.mediaType.getMediaType(),
                    )
                }
            )
        }
    }
}
