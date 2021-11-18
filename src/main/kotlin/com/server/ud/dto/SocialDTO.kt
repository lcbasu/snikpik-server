package com.server.ud.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.server.ud.entities.social.SocialRelation

@JsonIgnoreProperties(ignoreUnknown = true)
data class SocialRelationRequest (
    var toUserId: String,
    var following: Boolean,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SocialRelationResponse (
    var fromUserId: String,
    var toUserId: String,
    val following: Boolean,
)

fun SocialRelation.toSocialRelationResponse(): SocialRelationResponse {
    this.apply {
        return SocialRelationResponse(
            fromUserId = fromUserId,
            toUserId = toUserId,
            following = following,
        )
    }
}
