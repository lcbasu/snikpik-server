package com.server.common.controller

import com.server.common.service.AuthService
import com.server.dk.dto.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("auth")
class AuthController {
    @Autowired
    private lateinit var authService: AuthService

    @RequestMapping(value = ["/getAuthContext"], method = [RequestMethod.GET])
    fun getAuthContext(): RequestContextResponse {
        return authService.getAuthContext()
    }
}
