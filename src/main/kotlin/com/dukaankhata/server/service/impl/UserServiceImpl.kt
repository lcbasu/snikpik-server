package com.dukaankhata.server.service.impl

import com.dukaankhata.server.dto.SavedUserResponse
import com.dukaankhata.server.service.UserService
import com.dukaankhata.server.service.converter.UserServiceConverter
import com.dukaankhata.server.utils.AuthUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserServiceImpl : UserService() {

    @Autowired
    val authUtils: AuthUtils? = null

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
        return userServiceConverter?.getSavedUserResponse(user);
    }

    override fun getUser(): SavedUserResponse? {
        return super.getUser()
    }
}
