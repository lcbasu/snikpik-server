package com.server.ud.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.server.common.utils.DateUtils
import com.server.ud.entities.bookmark.Bookmark
import com.server.ud.entities.like.Like
import com.server.ud.enums.BookmarkUpdateAction
import com.server.ud.enums.ResourceType
import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import java.time.Instant

@JsonIgnoreProperties(ignoreUnknown = true)
data class SaveBookmarkRequest(
    var resourceType: ResourceType,
    var resourceId: String,
    var action: BookmarkUpdateAction
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SavedBookmarkResponse(
    val bookmarkId: String,
    val createdAt: Long,
    val resourceId: String,
    val resourceType: ResourceType,
    val userId: String,
    val bookmarked: Boolean,
)

fun Bookmark.toSavedBookmarkResponse(): SavedBookmarkResponse {
    this.apply {
        return SavedBookmarkResponse(
            bookmarkId = bookmarkId,
            createdAt = DateUtils.getEpoch(createdAt),
            resourceId = resourceId,
            resourceType = resourceType,
            userId = userId,
            bookmarked = bookmarked,
        )
    }
}

