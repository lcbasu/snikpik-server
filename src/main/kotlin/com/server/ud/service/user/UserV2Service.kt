package com.server.ud.service.user

import com.server.ud.dto.*

abstract class UserV2Service {
    abstract fun getUser(userId: String): SavedUserV2Response?
    abstract fun updateUserV2Handle(request: UpdateUserV2HandleRequest): SavedUserV2Response?
    abstract fun updateUserV2DP(request: UpdateUserV2DPRequest): SavedUserV2Response?
    abstract fun updateUserV2Profiles(request: UpdateUserV2ProfilesRequest): SavedUserV2Response?
    abstract fun updateUserV2Name(request: UpdateUserV2NameRequest): SavedUserV2Response?
    abstract fun updateUserV2Location(request: UpdateUserV2LocationRequest): SavedUserV2Response?
    abstract fun saveUserV2(): SavedUserV2Response?
}
