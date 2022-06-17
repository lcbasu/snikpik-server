package com.server.sp.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.server.common.dto.PaginationRequest
import com.server.common.dto.PaginationResponse
import com.server.common.model.MediaDetailsV2
import com.server.common.model.getMediaDetailsFromJsonString
import com.server.common.utils.DateUtils
import com.server.sp.entities.moment.SpMoment
import com.server.sp.enums.SpMomentMediaType
import com.server.sp.enums.SpMomentType
import com.server.sp.model.MomentTaggedUserDetails
import com.server.sp.model.toMomentTaggedUserDetails
import com.server.ud.dto.SavedPostResponse
import com.server.ud.entities.post.*

@JsonIgnoreProperties(ignoreUnknown = true)
data class UpdateMomentRequest (
    val momentId: String,
    val title: String? = null,
    val description: String? = null,
//    val mediaDetails: MediaDetailsV2? = null,
    val challengeId: String? = null,
    val momentTaggedUserDetails: MomentTaggedUserDetails,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class DeleteMomentRequest (
    val momentId: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SaveSpMomentRequest (
    val momentType: SpMomentType,
    val momentMediaType: SpMomentMediaType,
    val title: String? = null,
    val description: String? = null,
    val mediaDetails: MediaDetailsV2? = null,
    val challengeId: String? = null,

    // At least one person should be tagged
    val momentTaggedUserDetails: MomentTaggedUserDetails,
)

interface SpMomentMiniDetail {
    val momentId: String
    val userId: String
    val createdAt: Long
    val media: MediaDetailsV2?
    val title: String?
    val description: String?
    val challengeId: String?
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class SavedSpMomentResponse (
    val momentId: String,
    val momentType: SpMomentType,
    val momentMediaType: SpMomentMediaType,
    val userId: String,
    val createdAt: Long,

    val title: String? = null,
    val description: String? = null,

    val challengeId: String? = null,

    // To use the actual media urls wherever required
    val sourceMediaDetails: MediaDetailsV2? = null,

    val mediaDetails: MediaDetailsV2? = null,

    val momentTaggedUserDetails: MomentTaggedUserDetails,
)

fun SpMoment.toSavedSpMomentResponse(): SavedSpMomentResponse {
    this.apply {
        return SavedSpMomentResponse(
            momentId = momentId,
            momentType = momentType,
            momentMediaType = momentMediaType,
            userId = userId,
            createdAt = DateUtils.getEpoch(createdAt),

            title = title,
            description = description,

            challengeId = challengeId,

            // To use the actual media urls wherever required
            sourceMediaDetails = getMediaDetailsFromJsonString(sourceMedia),

            mediaDetails = getMediaDetailsFromJsonString(mediaDetails),

            momentTaggedUserDetails = momentTaggedUserDetails!!.toMomentTaggedUserDetails(),
        )
    }
}

data class SpMomentsByUserRequest (
    val userId: String,
    override val limit: Int = 10,
    override val pagingState: String? = null,
): PaginationRequest(limit, pagingState)

data class SpMomentsByUserResponse(
    val moments: List<SavedSpMomentResponse>,
    override val count: Int? = null,
    override val pagingState: String? = null,
    override val hasNext: Boolean? = null,
): PaginationResponse(count, pagingState, hasNext)
