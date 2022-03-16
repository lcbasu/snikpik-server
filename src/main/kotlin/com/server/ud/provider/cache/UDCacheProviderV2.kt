package com.server.ud.provider.cache

import com.server.dk.dto.UserReportResponse
import com.server.ud.dto.PostReportResponse
import com.server.ud.enums.PostReportActionType
import com.server.ud.enums.UserReportActionType
import com.server.ud.provider.post.PostProvider
import com.server.ud.provider.user.UserV2Provider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class UDCacheProviderV2 {

    @Autowired
    private lateinit var userV2Provider: UserV2Provider

    @Autowired
    private lateinit var postProvider: PostProvider

    // Move to Caffeine later
    fun getUserReports(userId: String) = userV2Provider.getAllReport(userId)
    fun getPostReports(userId: String) = postProvider.getAllReport(userId)

    fun getBlockedIds(userId: String): BlockedIDs {
        return BlockedIDs(
            postIds = getBlockedPostIds(userId),
            userIds = getBlockedUserIds(userId),
            mutedUserIds = getMutedUserIds(userId)
        )
    }

    private fun getBlockedPostIds(userId: String): Set<String> {
        val postReports: List<PostReportResponse> = userId?.let {
            getPostReports(userId)?.reports?.filter { it.action == PostReportActionType.SPAM } ?: emptyList()
        } ?: emptyList()
        return postReports.map { it.postId }.toSet()
    }

    fun getMutedUserIds(userId: String): Set<String> {
        val userReports: List<UserReportResponse> = userId?.let {
            getUserReports(userId)?.reports?.filter { it.action == UserReportActionType.MUTE_FEED } ?: emptyList()
        }
        return userReports.map { it.forUserId }.toSet()
    }

    private fun getBlockedUserIds(userId: String): Set<String> {
        val userReports: List<UserReportResponse> = userId?.let {
            getUserReports(userId)?.reports?.filter { it.action == UserReportActionType.BLOCK } ?: emptyList()
        }
        return userReports.map { it.forUserId }.toSet()
    }

    fun getBlockedOrMutedUserIds(userId: String): Set<String> {
        val userReports: List<UserReportResponse> = userId?.let {
            getUserReports(userId)?.reports?.filter { it.action == UserReportActionType.BLOCK || it.action == UserReportActionType.MUTE_FEED } ?: emptyList()
        }
        return userReports.map { it.forUserId }.toSet()
    }

}

data class BlockedIDs (
    val postIds: Set<String> = emptySet(),
    val userIds: Set<String> = emptySet(),
    val mutedUserIds: Set<String> = emptySet(),
)
