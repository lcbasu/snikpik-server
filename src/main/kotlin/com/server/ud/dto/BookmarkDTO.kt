package com.server.ud.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.server.ud.enums.BookmarkUpdateAction
import com.server.ud.enums.ResourceType

@JsonIgnoreProperties(ignoreUnknown = true)
data class SaveBookmarkRequest(
    var resourceType: ResourceType,
    var resourceId: String,
    var action: BookmarkUpdateAction
)
