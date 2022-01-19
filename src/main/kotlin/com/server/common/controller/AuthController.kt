package com.server.common.controller

import com.server.common.service.AuthService
import com.server.dk.dto.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("auth")
class AuthController {
    @Autowired
    private lateinit var authService: AuthService

    @RequestMapping(value = ["/getAuthContext"], method = [RequestMethod.GET])
    fun getAuthContext(): RequestContextResponse {
        return authService.getAuthContext()
    }

    @RequestMapping(value = ["/sendOTP"], method = [RequestMethod.POST])
    fun sendOTP(@RequestBody request: SendOTPRequest): OTPSentResponse {
        return authService.sendOTP(request)
    }

    @RequestMapping(value = ["/login"], method = [RequestMethod.POST])
    fun login(@RequestBody request: LoginRequest): LoginResponse {
        return authService.login(request)
    }
}
