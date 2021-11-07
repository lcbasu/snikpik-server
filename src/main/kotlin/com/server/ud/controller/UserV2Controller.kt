package com.server.ud.controller

import com.server.ud.dto.*
import com.server.ud.entities.user.UserV2
import com.server.ud.service.user.UserV2Service
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("ud/user")
class UserV2Controller {

    @Autowired
    private lateinit var userV2Service: UserV2Service

    @RequestMapping(value = ["/getUser"], method = [RequestMethod.GET])
    fun getUser(@RequestParam userId: String): SavedUserV2Response? {
        return userV2Service.getUser(userId)
    }

    @RequestMapping(value = ["/updateUserV2Handle"], method = [RequestMethod.POST])
    fun updateUserV2Handle(@RequestBody request: UpdateUserV2HandleRequest): SavedUserV2Response? {
        return userV2Service.updateUserV2Handle(request)
    }

    @RequestMapping(value = ["/updateUserV2DP"], method = [RequestMethod.POST])
    fun updateUserV2DP(@RequestBody request: UpdateUserV2DPRequest): SavedUserV2Response? {
        return userV2Service.updateUserV2DP(request)
    }

    @RequestMapping(value = ["/updateUserV2Profiles"], method = [RequestMethod.POST])
    fun updateUserV2Profiles(@RequestBody request: UpdateUserV2ProfilesRequest): SavedUserV2Response? {
        return userV2Service.updateUserV2Profiles(request)
    }

    @RequestMapping(value = ["/updateUserV2Name"], method = [RequestMethod.POST])
    fun updateUserV2Name(@RequestBody request: UpdateUserV2NameRequest): SavedUserV2Response? {
        return userV2Service.updateUserV2Name(request)
    }

    @RequestMapping(value = ["/updateUserV2Location"], method = [RequestMethod.POST])
    fun updateUserV2Location(@RequestBody request: UpdateUserV2LocationRequest): SavedUserV2Response? {
        return userV2Service.updateUserV2Location(request)
    }

}
