package com.server.ud.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.server.common.model.MediaDetailsV2
import com.server.common.model.getMediaDetailsFromJsonString
import com.server.common.utils.DateUtils
import com.server.ud.entities.integration.common.IntegrationAccountInfoByUserId
import com.server.ud.entities.post.InstagramPost
import com.server.ud.entities.post.getMediaDetails
import com.server.ud.enums.*

data class GetInstagramPostsRequest (
    val userId: String,
    val accountId: String,
    override val limit: Int = 10,
    override val pagingState: String? = null,
): PaginationRequest(limit, pagingState)


data class AllInstagramPostsResponse(
    val posts: List<SavedInstagramPostResponse>,
    override val count: Int? = null,
    override val pagingState: String? = null,
    override val hasNext: Boolean? = null,
): PaginationResponse(count, pagingState, hasNext)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SavedInstagramPostResponse (
    val userId: String,
    val accountId: String,
    val instagramPostId: String,
    val unboxPostId: String? = "",
    val mediaType: InstagramMediaType,
    val state: InstagramPostProcessingState,
    val createdAt: Long,
    val caption: String?,
    val mediaDetails: MediaDetailsV2?,
    val permalink: String?,
    val timestamp: String?,
    val username: String?,
)

data class ConnectInstagramAccountRequest (
    val userId: String,
    val code: String,
)

data class DisconnectInstagramAccountRequest (
    val userId: String,
    val instagramUserId: String,
)

data class StartInstagramIngestionRequest (
    val userId: String,
    val accountId: String,
    val postIdsNotWanted: Set<String>,
    val autoIngestFuturePosts: Boolean,
)

data class UpdateIngestionStateRequest (
    val userId: String,
    val accountId: String,
    val platform: IntegrationPlatform,
    val shouldPause: Boolean,
)

data class UpdateIngestionStateResponse (
    val userId: String,
    val integrationAccount: IntegrationAccountResponse?,
)

data class StartedInstagramIngestionResponse (
    val userId: String,
    val accountId: String,
    val started: Boolean
)

data class DisconnectInstagramAccountResponse (
    val userId: String,
    val instagramUserId: String,
    val disconnected: Boolean,
)

data class ConnectInstagramAccountResponse (
    val userId: String,
    val integrationAccount: IntegrationAccountResponse?,
    val error: Boolean?,
    val errorMessage: String?,
)

data class InstagramShortLivedAccessTokenResponse (
    val instagramAccessToken: String,
    val instagramUserId: String,
)

data class InstagramLongLivedAccessTokenResponse (
    val instagramAccessToken: String,
    val tokenType: String, // bearer
    val expiresIn: Long, // In Seconds
)

data class AllIntegrationAccountsResponse (
    val accounts: List<IntegrationAccountResponse>,
)

data class IntegrationAccountResponse (
    val userId: String,
    val platform: IntegrationPlatform,
    val accountId: String,
    val createdAt: Long,
    val syncType: IntegrationPlatformSyncType,
    val pauseIngestion: Boolean = false,
    val firstIngestionDone: Boolean = false,
    val userIdOnPlatform: String? = null,
    val usernameOnPlatform: String? = null,
    val emailOnPlatform: String? = null,
    val dpOnPlatform: MediaDetailsV2? = null, // MediaDetailsV2 as string
    val authorizationCode: String? = null,
    val shortLivedAccessToken: String? = null,
    val longLivedAccessToken: String? = null,
    val expiresIn: Long? = null, // In Seconds
)

fun IntegrationAccountInfoByUserId.toIntegrationAccountResponse(): IntegrationAccountResponse {
    this.apply {
        return IntegrationAccountResponse(
            userId = userId,
            platform = platform,
            accountId = accountId,
            createdAt = DateUtils.getEpoch(createdAt),
            syncType = syncType,
            pauseIngestion = pauseIngestion,
            firstIngestionDone = firstIngestionDone,
            userIdOnPlatform = userIdOnPlatform,
            usernameOnPlatform = usernameOnPlatform,
            emailOnPlatform = emailOnPlatform,
            dpOnPlatform = getMediaDetailsFromJsonString(dpOnPlatform),
            authorizationCode = authorizationCode,
            shortLivedAccessToken = shortLivedAccessToken,
            longLivedAccessToken = longLivedAccessToken,
            expiresIn = expiresIn,
        )
    }
}


fun InstagramPost.toSavedInstagramPostResponse(): SavedInstagramPostResponse {
    this.apply {
        return SavedInstagramPostResponse(
            userId = userId,
            accountId = accountId,
            instagramPostId = postId,
            unboxPostId = unboxPostId,
            mediaType = mediaType,
            state = state,
            createdAt = DateUtils.getEpoch(createdAt),
            caption = caption,
            mediaDetails = getMediaDetails(),
            permalink = permalink,
            timestamp = timestamp,
            username = username,
        )
    }
}

fun IntegrationAccountInfoByUserId.toConnectInstagramAccountResponse(error: Boolean? = false, errorMessage: String? = null): ConnectInstagramAccountResponse {
    this.apply {
        return ConnectInstagramAccountResponse(
            userId = userId,
            integrationAccount = toIntegrationAccountResponse(),
            error = error,
            errorMessage = errorMessage,
        )
    }
}
