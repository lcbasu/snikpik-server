package com.server.dk.dto

import com.server.dk.enums.CategoryGroupId
import com.server.common.model.MediaDetails
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class CategoryGroupResponse(
    val id: CategoryGroupId,
    val displayName: String,
    val description: String,
    val mediaDetails: MediaDetails
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class CategoryGroupsResponse(
    val groups: List<CategoryGroupResponse>
)
