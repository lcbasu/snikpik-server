package com.dukaankhata.server.service.converter

import com.dukaankhata.server.dto.SavedUserResponse
import com.dukaankhata.server.entities.User
import org.springframework.stereotype.Component

@Component
class UserServiceConverter {

    fun getSavedUserResponse(user: User?): SavedUserResponse {
        return SavedUserResponse(
            serverId = user?.id ?: "",
            name = user?.fullName ?: "",
            uid = user?.uid ?: "",
            phoneNumber = user?.id ?: "")
    }

}
