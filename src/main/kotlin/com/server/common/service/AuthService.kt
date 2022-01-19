package com.server.common.service

import com.server.dk.dto.*

abstract class AuthService {
    abstract fun getAuthContext(): RequestContextResponse
    abstract fun login(request: LoginRequest): LoginResponse
    abstract fun sendOTP(request: SendOTPRequest): OTPSentResponse
    abstract fun refreshToken(): LoginResponse
}
