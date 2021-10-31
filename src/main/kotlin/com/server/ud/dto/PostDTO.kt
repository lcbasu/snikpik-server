package com.server.ud.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.server.ud.entities.post.Post
import com.server.ud.enums.CategoryV2
import com.server.ud.enums.PostType
import com.server.ud.model.HashTagData
import com.server.ud.model.HashTagsList

@JsonIgnoreProperties(ignoreUnknown = true)
data class FakePostRequest(
    var countOfPost: Int,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SavePostRequest(
    var postType: PostType,
    var title: String? = null,
    var description: String? = null,
    val tags: HashTagsList = HashTagsList(emptyList()),
    val categories: Set<CategoryV2> = emptySet(),
    val locationRequest: SaveLocationRequest? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SavedPostResponse(
    var postId: String? = null,
    var userId: String? = null,
    var locationId: String? = null,
    var createdAt: Long? = null,
    var title: String? = null,
    var description: String? = null
)

fun Post.toSavedUserPostResponse(): SavedPostResponse {
    this.apply {
        return SavedPostResponse(
            postId = postId,
            userId = userId,
            locationId = locationId,
            createdAt = createdAt.toEpochMilli(),
            title = title,
            description = description,
        )
    }
}
