package com.server.ud.controller

import com.server.common.provider.SecurityProvider
import com.server.ud.dto.ByUserActivitiesFeedRequest
import com.server.ud.dto.ForUserActivitiesFeedRequest
import com.server.ud.dto.UserActivitiesFeedResponse
import com.server.ud.enums.UserAggregateActivityType
import com.server.ud.service.user_activity.UserActivitiesService
import io.micrometer.core.annotation.Timed
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@Timed
@RequestMapping("ud/userActivities")
class UserActivitiesController {

    @Autowired
    private lateinit var securityProvider: SecurityProvider

    @Autowired
    private lateinit var userActivitiesService: UserActivitiesService


    @RequestMapping(value = ["/getActivitiesFeedForUser"], method = [RequestMethod.GET])
    fun getActivitiesFeedForUser(@RequestParam forUserId: String,
                                 @RequestParam userAggregateActivityType: UserAggregateActivityType?,
                                 @RequestParam limit: Int,
                                 @RequestParam pagingState: String?): UserActivitiesFeedResponse? {
        securityProvider.validateRequest()
        return userActivitiesService.getActivitiesFeedForUser(
            ForUserActivitiesFeedRequest(
                forUserId,
                userAggregateActivityType,
                limit,
                pagingState
            )
        )
    }

    @RequestMapping(value = ["/getActivitiesFeedByUser"], method = [RequestMethod.GET])
    fun getActivitiesFeedByUser(@RequestParam byUserId: String,
                                @RequestParam userAggregateActivityType: UserAggregateActivityType?,
                                @RequestParam limit: Int,
                                @RequestParam pagingState: String?): UserActivitiesFeedResponse? {
        securityProvider.validateRequest()
        return userActivitiesService.getActivitiesFeedByUser(
            ByUserActivitiesFeedRequest(
                byUserId,
                userAggregateActivityType,
                limit,
                pagingState
            )
        )
    }

}
