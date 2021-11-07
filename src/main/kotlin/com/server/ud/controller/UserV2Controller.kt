package com.server.ud.controller

import com.server.ud.entities.user.UserV2
import com.server.ud.service.user.UserV2Service
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("ud/user")
class UserV2Controller {

    @Autowired
    private lateinit var userV2Service: UserV2Service

    @RequestMapping(value = ["/getUser"], method = [RequestMethod.GET])
    fun getUser(@RequestParam userId: String): UserV2? {
        return userV2Service.getUser(userId)
    }

}
