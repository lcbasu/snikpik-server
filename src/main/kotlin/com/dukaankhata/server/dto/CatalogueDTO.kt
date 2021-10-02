package com.dukaankhata.server.dto

import com.dukaankhata.server.enums.CategoryGroupId
import com.dukaankhata.server.model.MediaDetails
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
