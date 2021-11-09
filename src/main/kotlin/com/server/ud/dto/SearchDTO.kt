package com.server.ud.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.server.ud.entities.es.post.ESPost
import com.server.ud.entities.post.Post
import org.elasticsearch.search.SearchHit


@JsonIgnoreProperties(ignoreUnknown = true)
data class UDSearchRequest(
    override val typedText: String,
    override val from: Int,
    override val size: Int
): PaginationSearchRequest(typedText, from, size)

@JsonIgnoreProperties(ignoreUnknown = true)
data class PostsSearchResponse(
    var posts: List<ESPost>,
    override val typedText: String,
    override val from: Int,
    override val size: Int,
    override val numFound: Long,
): PaginationSearchResponse(typedText, from, size, numFound)



fun SearchHit.getESPost(): ESPost? {
    this.apply {
        return try {
            jacksonObjectMapper().readValue(this.sourceAsString, ESPost::class.java)
        } catch (e: Exception) {
            null
        }
    }
}
