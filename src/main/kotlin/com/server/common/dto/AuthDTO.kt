package com.server.dk.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.server.common.model.RequestContext

@JsonIgnoreProperties(ignoreUnknown = true)
data class RequestContextResponse(
    val userId: String,
    val uid: String?,
    val anonymous: Boolean,
)

fun RequestContext.toRequestContextResponse(): RequestContextResponse {
    this.apply {
        return RequestContextResponse(
            userId = user.id,
            uid = user.uid,
            anonymous = user.anonymous
        )
    }
}
