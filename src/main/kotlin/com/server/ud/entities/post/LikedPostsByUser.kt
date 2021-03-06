package com.server.ud.entities.post

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.server.common.model.MediaDetailsV2
import com.server.common.model.getMediaDetailsFromJsonString
import com.server.common.utils.DateUtils
import com.server.ud.dto.LikedPostsByUserPostDetail
import com.server.ud.enums.PostType
import com.server.ud.model.AllHashTags
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.time.Instant

// For Profile Page integration
@Table("liked_posts_by_user")
data class LikedPostsByUser (

    // A single post could have millions of saves. Hence, partitioning that date wise
    @PrimaryKeyColumn(name = "user_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var userId: String,

    @PrimaryKeyColumn(name = "post_type", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    var postType: PostType,

    @PrimaryKeyColumn(name = "post_id", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
    var postId: String,

    @Column("created_at")
    var createdAt: Instant = DateUtils.getInstantNow(),

    @Column("post_created_at")
    var postCreatedAt: Instant,

    @Column("posted_by_user_id")
    var postedByUserId: String,

    @Column
    var title: String? = null,

    @Column
    var description: String? = null,

    @Column
    var media: String? = null, // MediaDetailsV2

    @Column("source_media")
    var sourceMedia: String? = null, // MediaDetailsV2

    @Column
    var tags: String? = null, // List of HashTagList

    @Column
    var categories: String? = null, //  List of CategoryV2
)

@Table("liked_posts_by_user_tracker")
data class LikedPostsByUserTracker (

    @PrimaryKeyColumn(name = "post_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var postId: String,

    @PrimaryKeyColumn(name = "user_id", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    var userId: String,

    @PrimaryKeyColumn(name = "post_type", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
    var postType: PostType,

    @Column("created_at")
    var createdAt: Instant = DateUtils.getInstantNow(),

    @Column("post_created_at")
    var postCreatedAt: Instant,

    @Column("posted_by_user_id")
    var postedByUserId: String,

    @Column
    var title: String? = null,

    @Column
    var description: String? = null,

    @Column
    var media: String? = null, // MediaDetailsV2

    @Column("source_media")
    var sourceMedia: String? = null, // MediaDetailsV2

    @Column
    var tags: String? = null, // List of HashTagList

    @Column
    var categories: String? = null, //  List of CategoryV2
)

fun LikedPostsByUser.toLikedPostsByUserTracker(): LikedPostsByUserTracker {
    this.apply {
        return LikedPostsByUserTracker(
            userId = this.userId,
            postType = this.postType,
            postId = this.postId,
            createdAt = this.createdAt,
            postCreatedAt = this.postCreatedAt,
            postedByUserId = this.postedByUserId,
            title = this.title,
            description = this.description,
            media = this.media,
            sourceMedia = this.sourceMedia,
            tags = this.tags,
            categories = this.categories
        )
    }
}


fun LikedPostsByUserTracker.toLikedPostsByUser(): LikedPostsByUser {
    this.apply {
        return LikedPostsByUser(
            userId = this.userId,
            postType = this.postType,
            postId = this.postId,
            createdAt = this.createdAt,
            postCreatedAt = this.postCreatedAt,
            postedByUserId = this.postedByUserId,
            title = this.title,
            description = this.description,
            media = this.media,
            sourceMedia = this.sourceMedia,
            tags = this.tags,
            categories = this.categories
        )
    }
}

fun LikedPostsByUser.getMediaDetails(): MediaDetailsV2? {
    this.apply {
        return getMediaDetailsFromJsonString(media)
    }
}

fun LikedPostsByUser.getSourceMediaDetails(): MediaDetailsV2? {
    this.apply {
        return getMediaDetailsFromJsonString(sourceMedia)
    }
}

fun LikedPostsByUser.getHashTags(): AllHashTags {
    this.apply {
        return try {
            jacksonObjectMapper().readValue(tags, AllHashTags::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            AllHashTags(emptySet())
        }
    }
}


fun LikedPostsByUser.toLikedPostsByUserPostDetail(): LikedPostsByUserPostDetail {
    this.apply {
        return LikedPostsByUserPostDetail(
            postId = postId,
            userId = postedByUserId,
            media = getMediaDetails(),
            title = title,
            createdAt = DateUtils.getEpoch(postCreatedAt),
            likedAt = DateUtils.getEpoch(createdAt),
            likedByUserId = userId,
            description = description,
        )
    }
}
