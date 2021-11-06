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
