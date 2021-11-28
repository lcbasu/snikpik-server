package com.server.ud.model

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

data class AllHashTags (
    val tags: Set<String>
)

fun AllHashTags.convertToString(): String? {
    this.apply {
        return try {
            jacksonObjectMapper().writeValueAsString(this)
        } catch (e: Exception) {
            null
        }
    }
}

val sampleHashTagsIds = setOf("newhouse", "lakesideview", "bigkitchen", "tenniscourt").shuffled()
