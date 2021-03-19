package com.dukaankhata.server.service.impl

import com.dukaankhata.server.dto.SavedUserResponse
import com.dukaankhata.server.dto.UserRoleResponse
import com.dukaankhata.server.service.UserService
import com.dukaankhata.server.service.converter.UserServiceConverter
import com.dukaankhata.server.utils.AuthUtils
import com.dukaankhata.server.utils.UserRoleUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserServiceImpl : UserService() {

    @Autowired
    val authUtils: AuthUtils? = null

    @Autowired
    val userRoleUtils: UserRoleUtils? = null

    @Autowired
    val userServiceConverter: UserServiceConverter? = null

    // Save data using the auth token credentials
    override fun saveUser(): SavedUserResponse? {
        val firebaseAuthUserPrincipal = authUtils?.getFirebaseAuthUser()
        val phoneNumber = firebaseAuthUserPrincipal?.getPhoneNumber() ?: ""
        val uid = firebaseAuthUserPrincipal?.getUid() ?: ""
        // if already saved then return that value or save a new one
        var user = authUtils?.getRequestUserEntity()
        if (user == null) {
            user = authUtils?.createUser(phoneNumber = phoneNumber, fullName = phoneNumber, uid = uid)
        } else if (user.uid.isBlank()) {
            user = authUtils?.updateUserUid(user.id, uid)
        }
        return userServiceConverter?.getSavedUserResponse(user)
    }

    override fun getUser(): SavedUserResponse? {
        TODO("Not yet implemented")
    }

    override fun getUserRoles(phoneNumber: String): UserRoleResponse? {
        val user = authUtils?.getUserByPhoneNumber(phoneNumber);
        val userRoles = user?.let { userRoleUtils?.getUserRolesForUser(it) } ?: emptyList()
        return userServiceConverter?.getUserRolesResponse(userRoles)
    }
}
