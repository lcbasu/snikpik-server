package com.server.ud.entities.post

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.server.common.utils.DateUtils
import com.server.dk.model.MediaDetailsV2
import com.server.ud.enums.CategoryV2
import com.server.ud.enums.PostType
import com.server.ud.model.HashTagsList
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.time.Instant

// For Profile Page integration
@Table("bookmarked_posts_by_user")
class BookmarkedPostsByUser (

    // A single post could have millions of saves. Hence, partitioning that date wise
    @PrimaryKeyColumn(name = "user_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var userId: String,

    @PrimaryKeyColumn(name = "post_type", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
    var postType: PostType,

    // Only get the ones that are marked as true
    // Doing this as it is hard to delete in cassandra if someone un-saves
    @PrimaryKeyColumn(name = "bookmarked", ordinal = 2, type = PrimaryKeyType.PARTITIONED)
    var bookmarked: Boolean,

    @PrimaryKeyColumn(name = "post_id", ordinal = 3, type = PrimaryKeyType.CLUSTERED)
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

    @Column
    var tags: String? = null, // List of HashTagList

    @Column
    var categories: String? = null, //  List of CategoryV2
)

fun BookmarkedPostsByUser.getMediaDetails(): MediaDetailsV2? {
    this.apply {
        return try {
            jacksonObjectMapper().readValue(media, MediaDetailsV2::class.java)
        } catch (e: Exception) {
            null
        }
    }
}

fun BookmarkedPostsByUser.getHashTags(): HashTagsList {
    this.apply {
        return try {
            return jacksonObjectMapper().readValue(tags, HashTagsList::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            HashTagsList(emptyList())
        }
    }
}

fun BookmarkedPostsByUser.getCategories(): Set<CategoryV2> {
    this.apply {
        return try {
            val categoryIds = categories?.trim()?.split(",") ?: emptySet()
            return categoryIds.map {
                CategoryV2.valueOf(it)
            }.toSet()
        } catch (e: Exception) {
            e.printStackTrace()
            emptySet()
        }
    }
}

