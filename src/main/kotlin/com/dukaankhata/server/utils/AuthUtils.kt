package com.dukaankhata.server.utils

import com.dukaankhata.server.dao.UserRepository
import com.dukaankhata.server.entities.User
import com.dukaankhata.server.enums.Gender
import com.dukaankhata.server.model.FirebaseAuthUser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import java.util.*

@Component
class AuthUtils {

    @Autowired
    var userRepository: UserRepository? = null


    fun getFirebaseAuthUser(): FirebaseAuthUser? {
        var firebaseAuthUserPrincipal: FirebaseAuthUser? = null
        val securityContext = SecurityContextHolder.getContext()
        val principal = securityContext.authentication.principal
        if (principal is FirebaseAuthUser) {
            firebaseAuthUserPrincipal = principal as FirebaseAuthUser
        }
        return firebaseAuthUserPrincipal
    }

    fun getRequestUserEntity(): User? {
        val firebaseAuthUserPrincipal = getFirebaseAuthUser()
        val phoneNumber = firebaseAuthUserPrincipal?.getPhoneNumber() ?: ""
        userRepository?.let {
            val user = it.findById(phoneNumber)
            if (user.isPresent && user.get().id.isNotEmpty()) {
                return user.get()
            }
        }
        return null
    }

    fun updateUserUid(id: String, uid: String): User? {
        return userRepository?.let {
            val userOptional = it.findById(id)
            if (userOptional.isPresent) {
                val user = userOptional.get()
                user.uid = uid
                it.save(user)
            } else {
                error("No user found with the id: $id")
            }
        }
    }


    fun createUser(phoneNumber: String, fullName: String, uid: String): User? {
        return userRepository?.let { val newUser = User()
            newUser.id = phoneNumber
            newUser.fullName = fullName
            newUser.gender = Gender.MALE
            newUser.uid = uid
            it.save(newUser)
        }
    }

    fun getOrCreateUserByPhoneNumber(phoneNumber: String): User? {
        return userRepository?.let {
            val user = it.findById(phoneNumber)
            if (user.isPresent && user.get().id.isNotEmpty()) {
                return user.get()
            }
            return createUser(phoneNumber = phoneNumber, fullName = phoneNumber, uid = "")
        }
    }

}
