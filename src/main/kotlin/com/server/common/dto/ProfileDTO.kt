package com.server.common.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.server.common.enums.ProfileCategory
import com.server.common.enums.ProfileType
import com.server.dk.model.MediaDetailsV2

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
