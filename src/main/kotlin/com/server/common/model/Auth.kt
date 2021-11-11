package com.server.common.model

import com.server.common.entities.User
import com.server.dk.entities.Company
import com.server.dk.entities.Employee
import com.server.dk.entities.UserRole
import com.server.ud.entities.user.UserV2


//data class RequestContext (
//    val user: User,
//    val userV2: UserV2,
//    val company: Company? = null,
//    val employee: Employee? = null,
//    val userRoles: List<UserRole> = emptyList(),
//)
data class RequestContext (
    val user: User,
    val userV2: UserV2,
    val company: Company? = null,
    val employee: Employee? = null,
    val userRoles: List<UserRole> = emptyList(),
)
