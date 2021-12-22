package com.server.ud.service.user_activity

import com.server.ud.dto.ByUserActivitiesFeedRequest
import com.server.ud.dto.ForUserActivitiesFeedRequest
import com.server.ud.dto.UserActivitiesFeedResponse
import com.server.ud.dto.toUserActivityResponse
import com.server.ud.provider.user_activity.UserActivitiesProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserActivitiesServiceImpl: UserActivitiesService() {

    @Autowired
    private lateinit var userActivitiesProvider: UserActivitiesProvider

    override fun getActivitiesFeedForUser(request: ForUserActivitiesFeedRequest): UserActivitiesFeedResponse? {
        val result = userActivitiesProvider.getActivitiesFeedForUser(request)
        return UserActivitiesFeedResponse(
            activities = result.content?.filterNotNull()?.mapNotNull { it.toUserActivityResponse() } ?: emptyList(),
            count = result.count,
            hasNext = result.hasNext,
            pagingState = result.pagingState
        )
    }

    override fun getActivitiesFeedByUser(request: ByUserActivitiesFeedRequest): UserActivitiesFeedResponse? {
        val result = userActivitiesProvider.getActivitiesFeedByUser(request)
        return UserActivitiesFeedResponse(
            activities = result.content?.filterNotNull()?.mapNotNull { it.toUserActivityResponse() } ?: emptyList(),
            count = result.count,
            hasNext = result.hasNext,
            pagingState = result.pagingState
        )
    }
}
