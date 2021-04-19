package com.dukaankhata.server.service.converter

import com.dukaankhata.server.dto.UserRoleResponse
import com.dukaankhata.server.entities.UserRole
import org.springframework.stereotype.Component

@Component
class UserServiceConverter {

    fun getUserRolesResponse(userRoles: List<UserRole>): UserRoleResponse {
        val roles = userRoles.filter { it.id != null && it.id?.roleType != null }.map { it.id?.roleType }
        return UserRoleResponse(roles = roles.filterNotNull().toSet())
    }

}
