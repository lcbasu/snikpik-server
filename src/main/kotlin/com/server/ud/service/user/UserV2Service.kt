package com.server.ud.service.user

import com.server.ud.entities.user.UserV2

//import com.server.dk.dto.SavedUserV2Response

abstract class UserV2Service {
    abstract fun getUser(userId: String): UserV2?
}
