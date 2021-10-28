package com.server.ud.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.server.ud.entities.post.Post
import com.server.ud.enums.PostType

@JsonIgnoreProperties(ignoreUnknown = true)
data class SavePostRequest(
    var postType: PostType,
    var title: String? = null,
    var description: String? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SavedPostResponse(
    var postId: String? = null,
    var userId: String? = null,
    var createdAt: Long? = null,
    var title: String? = null,
    var description: String? = null
)

fun Post.toSavedUserPostResponse(): SavedPostResponse {
    this.apply {
        return SavedPostResponse(
            postId = postId,
            userId = userId,
            createdAt = createdAt.toEpochMilli(),
            title = title,
            description = description,
        )
    }
}
