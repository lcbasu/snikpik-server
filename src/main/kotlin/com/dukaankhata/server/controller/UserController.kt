package com.dukaankhata.server.controller

import com.dukaankhata.server.dto.SavedUserResponse
import com.dukaankhata.server.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("user")
class UserController {
    @Autowired
    var userService: UserService? = null

    @RequestMapping(value = ["/save"], method = [RequestMethod.POST])
    fun saveUser(): SavedUserResponse? {
        return userService?.saveUser()
    }
}
