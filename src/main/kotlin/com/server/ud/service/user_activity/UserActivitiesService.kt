package com.server.ud.service.user_activity

import com.server.ud.dto.ByUserActivitiesFeedRequest
import com.server.ud.dto.ForUserActivitiesFeedRequest
import com.server.ud.dto.UserActivitiesFeedResponse

abstract class UserActivitiesService {
    abstract fun getActivitiesFeedForUser(request: ForUserActivitiesFeedRequest): UserActivitiesFeedResponse?
    abstract fun getActivitiesFeedByUser(request: ByUserActivitiesFeedRequest): UserActivitiesFeedResponse?
}
