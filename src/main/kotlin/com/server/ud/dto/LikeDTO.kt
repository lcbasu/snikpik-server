package com.server.ud.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.server.ud.enums.LikeUpdateAction
import com.server.ud.enums.ResourceType

@JsonIgnoreProperties(ignoreUnknown = true)
data class SaveLikeRequest(
    var resourceType: ResourceType,
    var resourceId: String,
    var action: LikeUpdateAction
)
