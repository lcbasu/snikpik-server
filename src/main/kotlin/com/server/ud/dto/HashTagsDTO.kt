package com.server.ud.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class SaveHashTagsRequest(
    val tags: Set<String>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SavedHashTagsResponse(
    var tags: Set<String>,
)
