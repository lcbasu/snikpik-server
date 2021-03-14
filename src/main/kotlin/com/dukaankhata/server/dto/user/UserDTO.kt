package com.dukaankhata.server.dto.user

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

//@JsonIgnoreProperties(ignoreUnknown = true)
//data class SaveUserRequest(
//    val name: String,
//    val uid: String,
//    val phoneNumber: String,
//)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SavedUserResponse(
    val serverId: String,
    val name: String,
    val uid: String,
    val phoneNumber: String,
)

