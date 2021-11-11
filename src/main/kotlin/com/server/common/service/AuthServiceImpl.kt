package com.server.common.service

import com.server.common.provider.AuthProvider
import com.server.dk.dto.RequestContextResponse
import com.server.dk.dto.toRequestContextResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AuthServiceImpl : AuthService() {

    @Autowired
    private lateinit var authProvider: AuthProvider

    override fun getAuthContext(): RequestContextResponse {
        val requestContext = authProvider.validateRequest()
        return requestContext.toRequestContextResponse()
    }

}
