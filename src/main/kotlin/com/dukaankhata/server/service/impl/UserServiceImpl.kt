package com.dukaankhata.server.service.impl

import com.dukaankhata.server.dao.UserRepository
import com.dukaankhata.server.dto.user.SavedUserResponse
import com.dukaankhata.server.entities.User
import com.dukaankhata.server.enums.Gender
import com.dukaankhata.server.service.UserService
import com.dukaankhata.server.service.converter.UserServiceConverter
import com.dukaankhata.server.utils.AuthUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserServiceImpl : UserService() {

    @Autowired
    var userRepository: UserRepository? = null

    @Autowired
    val authUtils: AuthUtils? = null

    @Autowired
    val userServiceConverter: UserServiceConverter? = null

    // Save data using the auth token credentials
    override fun saveUser(): SavedUserResponse? {
        // if already saved then return that value or save a new one
        var user = authUtils?.getRequestUserEntity()
        if (user == null) {
            user = userRepository?.let {
                val firebaseAuthUserPrincipal = authUtils?.getFirebaseAuthUser()
                val phoneNumber = firebaseAuthUserPrincipal?.getPhoneNumber() ?: ""
                val uid = firebaseAuthUserPrincipal?.getUid() ?: ""
                val newUser = User()
                newUser.id = phoneNumber
                newUser.fullName = phoneNumber
                newUser.gender = Gender.MALE
                newUser.uid = uid
                it.save(newUser)
            }
        }
        return userServiceConverter?.getSavedUserResponse(user);
    }

    override fun getUser(): SavedUserResponse? {
        return super.getUser()
    }
}
