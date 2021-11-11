package com.server.common.service

import com.server.dk.dto.RequestContextResponse

abstract class AuthService {
    abstract fun getAuthContext(): RequestContextResponse
}
