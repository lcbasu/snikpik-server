package com.server.common.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.server.common.enums.ProfileCategory
import com.server.common.enums.ProfileType
import com.server.common.model.MediaDetailsV2
import com.server.ud.dto.MarketplaceUserDetail
import com.server.ud.dto.UserV2PublicMiniDataResponse

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

@JsonIgnoreProperties(ignoreUnknown = true)
data class ProfileTypeWithUsersResponse(
    val profileTypeToShow: ProfileTypeResponse,
    val users: List<MarketplaceUserDetail>,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ProfileTypeWithUsersResponseV3(
    val profileTypeToShow: ProfileTypeResponse,
    val users: List<UserV2PublicMiniDataResponse>,
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
