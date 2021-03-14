package com.dukaankhata.server.service.impl

import com.dukaankhata.server.dao.UserRepository
import com.dukaankhata.server.dto.user.SaveUserRequest
import com.dukaankhata.server.dto.user.SavedUserResponse
import com.dukaankhata.server.entities.User
import com.dukaankhata.server.enums.Gender
import com.dukaankhata.server.model.FirebaseAuthUser
import com.dukaankhata.server.service.UserService
import com.dukaankhata.server.service.converter.UserServiceConverter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class UserServiceImpl : UserService() {

    @Autowired
    var userRepository: UserRepository? = null

    @Autowired
    val userServiceConverter: UserServiceConverter? = null

    override fun saveUser(saveUserRequest: SaveUserRequest): SavedUserResponse? {
        val user = userRepository?.let {
            var firebaseAuthUserPrincipal: FirebaseAuthUser? = null
            val securityContext = SecurityContextHolder.getContext()
            val principal = securityContext.authentication.principal
            if (principal is FirebaseAuthUser) {
                firebaseAuthUserPrincipal = principal as FirebaseAuthUser
            }
            val phoneNumber = firebaseAuthUserPrincipal?.getPhoneNumber() ?: ""
            val uid = firebaseAuthUserPrincipal?.getUid() ?: ""
            val userOptional = it.findById(phoneNumber)
            if (userOptional.isPresent) {
                userOptional.get()
            } else {
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
