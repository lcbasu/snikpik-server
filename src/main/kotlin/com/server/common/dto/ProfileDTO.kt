package com.server.common.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.server.common.enums.ProfileCategory
import com.server.common.enums.ProfileType
import com.server.common.model.MediaDetailsV2

@JsonIgnoreProperties(ignoreUnknown = true)
data class AllLabelsResponse(
    val labels: Set<String>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AllProfileTypeResponse(
    val profileTypes: List<ProfileTypeResponse>
)

fun AllLabelsResponse.convertToString(): String? {
    this.apply {
        return try {
            jacksonObjectMapper().writeValueAsString(this)
        } catch (e: Exception) {
            null
        }
    }
}

fun AllProfileTypeResponse.convertToString(): String? {
    this.apply {
        return try {
            jacksonObjectMapper().writeValueAsString(this)
        } catch (e: Exception) {
            null
        }
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class ProfileTypeResponse(
    val id: ProfileType,
    val category: ProfileCategory,
    val displayName: String,
    val media: MediaDetailsV2?
)

fun ProfileType.toProfileTypeResponse(): ProfileTypeResponse {
    this.apply {
        return ProfileTypeResponse(
            id = this,
            category = category,
            displayName = displayName,
            media = media,
        )
    }
}
