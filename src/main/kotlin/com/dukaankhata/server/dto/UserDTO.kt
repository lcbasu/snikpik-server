package com.dukaankhata.server.dto

import com.dukaankhata.server.entities.Company
import com.dukaankhata.server.entities.Employee
import com.dukaankhata.server.entities.User
import com.dukaankhata.server.entities.UserRole
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class SavedUserResponse(
    val serverId: String,
    val name: String,
    val uid: String,
    val phoneNumber: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class UserRoleResponse(
    val roles: Set<String>,
)

data class RequestContext (
    val user: User,
    val company: Company? = null,
    val employee: Employee? = null,
    val userRoles: List<UserRole> = emptyList(),
)
