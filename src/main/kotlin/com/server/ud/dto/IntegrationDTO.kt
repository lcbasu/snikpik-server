package com.server.ud.dto

import com.server.ud.enums.InstagramAuthProcessingState
import com.server.ud.enums.IntegrationPlatform

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
    val accountId: String,
    val platform: IntegrationPlatform,
    val paused: Boolean,
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
    val code: String,
    val state: InstagramAuthProcessingState,
    val createdAt: Long,
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
