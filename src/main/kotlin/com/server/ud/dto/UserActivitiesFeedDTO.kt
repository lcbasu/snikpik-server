package com.server.ud.dto

import com.server.common.utils.DateUtils
import com.server.ud.entities.user_activity.UserActivityByUser
import com.server.ud.entities.user_activity.UserActivityForUser
import com.server.ud.entities.user_activity.getUserActivityData
import com.server.ud.enums.UserActivityType
import com.server.ud.enums.UserAggregateActivityType
import com.server.ud.model.UserActivityData

data class UserActivityResponse (
    var userAggregateActivityType: UserAggregateActivityType,
    var userActivityType: UserActivityType,
    var createdAt: Long,
    var byUserId: String,
    var userActivityData: UserActivityData,
    var forUserId: String?,
    var userActivityId: String,
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
