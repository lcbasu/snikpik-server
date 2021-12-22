package com.server.ud.dto

import com.server.common.model.MediaDetailsV2
import com.server.common.utils.DateUtils
import com.server.ud.entities.user.UserV2
import com.server.ud.entities.user.getMediaDetailsForDP
import com.server.ud.entities.user_activity.UserActivityByUser
import com.server.ud.entities.user_activity.UserActivityForUser
import com.server.ud.entities.user_activity.getUserActivityData
import com.server.ud.enums.UserActivityType
import com.server.ud.enums.UserAggregateActivityType
import com.server.ud.model.UserActivityData

data class ActivityByUserData (
    val userId: String,
    val name: String?,
    val handle: String?,
    val dp: MediaDetailsV2?,
)

data class UserActivityResponse (
    val userAggregateActivityType: UserAggregateActivityType,
    val userActivityType: UserActivityType,
    val createdAt: Long,
    val byUserId: String,
    val userActivityData: UserActivityData,
    val forUserId: String?,
    val userActivityId: String,
)

data class UserActivitiesFeedResponse (
    val activities: List<UserActivityResponse>,
    override val count: Int? = null,
    override val pagingState: String? = null,
    override val hasNext: Boolean? = null,
): PaginationResponse(count, pagingState, hasNext)

data class ForUserActivitiesFeedRequest (
    val forUserId: String,

    // If null, get all the data
    val userAggregateActivityType: UserAggregateActivityType? = null,
    override val limit: Int = 10,
    override val pagingState: String? = null, // YYYY-MM-DD
): PaginationRequest(limit, pagingState)

data class ByUserActivitiesFeedRequest (
    val byUserId: String,
    // If null, get all the data
    val userAggregateActivityType: UserAggregateActivityType? = null,
    override val limit: Int = 10,
    override val pagingState: String? = null, // YYYY-MM-DD
): PaginationRequest(limit, pagingState)

fun UserActivityForUser.toUserActivityResponse(): UserActivityResponse? {
    this.apply {
        return try {
            getUserActivityData()?.let {
                UserActivityResponse(
                    userAggregateActivityType = userAggregateActivityType,
                    userActivityType = userActivityType,
                    createdAt = DateUtils.getEpoch(createdAt),
                    byUserId = byUserId,
                    userActivityData = it,
                    forUserId = forUserId,
                    userActivityId = userActivityId,
                )
            }
        } catch (e: Exception) {
            null
        }
    }
}


fun UserActivityByUser.toUserActivityResponse(): UserActivityResponse? {
    this.apply {
        return try {
            getUserActivityData()?.let {
                UserActivityResponse(
                    userAggregateActivityType = userAggregateActivityType,
                    userActivityType = userActivityType,
                    createdAt = DateUtils.getEpoch(createdAt),
                    byUserId = byUserId,
                    userActivityData = it,
                    forUserId = forUserId,
                    userActivityId = userActivityId,
                )
            }
        } catch (e: Exception) {
            null
        }
    }
}

fun UserV2.toActivityByUserData(): ActivityByUserData? {
    this.apply {
        return try {
            ActivityByUserData(
                userId = userId,
                name = fullName,
                handle = handle,
                dp = getMediaDetailsForDP(),
            )
        } catch (e: Exception) {
            null
        }
    }
}
