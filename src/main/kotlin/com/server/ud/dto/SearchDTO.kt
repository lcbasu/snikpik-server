package com.server.ud.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.server.common.model.MediaDetailsV2


@JsonIgnoreProperties(ignoreUnknown = true)
data class UDSearchRequest(
    override val typedText: String,
    override val from: Int,
    override val size: Int
): PaginationSearchRequest(typedText, from, size)

data class SRPPostResponse(
    override val postId: String,
    override val userId: String,
    override val createdAt: Long,
    override val media: MediaDetailsV2?,
    override val title: String?,
    override val description: String?
): PostMiniDetail

@JsonIgnoreProperties(ignoreUnknown = true)
data class PostsSearchResponse(
    var posts: List<SRPPostResponse>,
    override val typedText: String,
    override val from: Int,
    override val size: Int,
    override val numFound: Long,
): PaginationSearchResponse(typedText, from, size, numFound)

//
//fun SearchHit.getESPost(): ESPost? {
//    this.apply {
//        return try {
//            jacksonObjectMapper().readValue(this.sourceAsString, ESPost::class.java)
//        } catch (e: Exception) {
//            null
//        }
//    }
//}
//
//fun ESPost.getPost(): SRPPostResponse {
//    this.apply {
//        return SRPPostResponse(
//            postId = postId,
//            userId = userId,
//            createdAt = createdAt,
//            media = getMediaDetails(),
//            title = title,
//        )
//    }
//}
