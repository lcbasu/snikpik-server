package com.server.ud.service.user

import com.server.common.provider.AuthProvider
import com.server.ud.dto.*
import com.server.ud.provider.user.UserV2Provider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserV2ServiceImpl : UserV2Service() {

    @Autowired
    private lateinit var authProvider: AuthProvider

    @Autowired
    private lateinit var userV2Provider: UserV2Provider

    override fun getUser(userId: String): SavedUserV2Response? {
        authProvider.validateRequest()
        return userV2Provider.getUser(userId)?.toSavedUserV2Response()
    }

    override fun updateUserV2Handle(request: UpdateUserV2HandleRequest): SavedUserV2Response? {
        authProvider.validateRequest()
        return userV2Provider.updateUserV2Handle(request)?.toSavedUserV2Response()
    }

    override fun updateUserV2DP(request: UpdateUserV2DPRequest): SavedUserV2Response? {
        authProvider.validateRequest()
        return userV2Provider.updateUserV2DP(request)?.toSavedUserV2Response()
    }

    override fun updateUserV2Profiles(request: UpdateUserV2ProfilesRequest): SavedUserV2Response? {
        authProvider.validateRequest()
        return userV2Provider.updateUserV2Profiles(request)?.toSavedUserV2Response()
    }

    override fun updateUserV2Name(request: UpdateUserV2NameRequest): SavedUserV2Response? {
        authProvider.validateRequest()
        return userV2Provider.updateUserV2Name(request)?.toSavedUserV2Response()
    }

    override fun updateUserV2Location(request: UpdateUserV2LocationRequest): SavedUserV2Response? {
        authProvider.validateRequest()
        return userV2Provider.updateUserV2Location(request)?.toSavedUserV2Response()
    }
}
