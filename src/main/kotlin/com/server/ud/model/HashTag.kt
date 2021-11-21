package com.server.ud.model

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

data class HashTagData (
    val tagId: String,
    val displayName: String
)

data class HashTagsList (
    val tags: List<HashTagData>
)

fun HashTagsList.convertToString(): String {
    this.apply {
        return jacksonObjectMapper().writeValueAsString(this)
    }
}

val sampleHashTags = listOf(
    HashTagData(
        tagId = "newhouse",
        displayName = "newhouse",
    ),
    HashTagData(
        tagId = "lakesideview",
        displayName = "lakesideview",
    ),
    HashTagData(
        tagId = "bigkitchen",
        displayName = "bigkitchen",
    ),
    HashTagData(
        tagId = "tenniscourt",
        displayName = "tenniscourt",
    )
)
