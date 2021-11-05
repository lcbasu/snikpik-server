package com.server.ud.controller

import com.server.ud.service.post.UserV2Service
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("ud/user")
class UserV2Controller {

    @Autowired
    private lateinit var userV2Service: UserV2Service

//    @RequestMapping(value = ["/getPosts"], method = [RequestMethod.GET])
//    fun getUserProfileInfo(@RequestParam userId: String): SavedUserV2Response {
//        return userService.getUserProfileInfo(userId)
//    }
}
