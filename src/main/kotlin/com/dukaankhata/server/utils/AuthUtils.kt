package com.dukaankhata.server.utils

import com.dukaankhata.server.dao.UserRepository
import com.dukaankhata.server.entities.User
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

}
