package com.dukaankhata.server.dto

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
