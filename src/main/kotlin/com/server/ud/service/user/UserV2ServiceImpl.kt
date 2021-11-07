package com.server.ud.service.user

import com.server.common.provider.AuthProvider
import com.server.ud.entities.user.UserV2
import com.server.ud.provider.user.UserV2Provider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserV2ServiceImpl : UserV2Service() {

    @Autowired
    private lateinit var authProvider: AuthProvider

    @Autowired
    private lateinit var userV2Provider: UserV2Provider

    override fun getUser(userId: String): UserV2? {
        authProvider.validateRequest()
        return userV2Provider.getUser(userId)
    }
}
