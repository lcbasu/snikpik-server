package com.server.ud.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.server.ud.entities.UserPost

@JsonIgnoreProperties(ignoreUnknown = true)
data class SaveUserPostRequest(
    var title: String? = null,
    var description: String? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SavedUserPostResponse(
    var postId: String? = null,
    var userId: String? = null,
    var postedAt: Long? = null,
    var title: String? = null,
    var description: String? = null
)

data class PostFeedResponse (
    val posts: List<SavedUserPostResponse>,
    val numFound: Long,
)

fun UserPost.toSavedUserPostResponse(): SavedUserPostResponse {
    this.apply {
        return SavedUserPostResponse(
            postId = postId,
            userId = userId,
            postedAt = postedAt.toEpochMilli(),
            title = title,
            description = description,
        )
    }
}
