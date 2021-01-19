package com.dukaankhata.server.controller

import com.dukaankhata.server.dao.UserRepository
import com.dukaankhata.server.entities.User
import com.dukaankhata.server.enums.Gender
import com.dukaankhata.server.model.FirebaseAuthUser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.util.*

@RestController
class HelloController {
    @RequestMapping("/")
    fun index(): String? {
        return "Greetings from Spring Boot!"
    }

    @Autowired
    var userRepository: UserRepository? = null

    @RequestMapping("/public/{id}")
    fun public(@PathVariable id: String): String? {

        userRepository?.let {

            var firebaseAuthUserPrincipal: FirebaseAuthUser? = null
            val securityContext = SecurityContextHolder.getContext()
            val principal = securityContext.authentication.principal
            if (principal is FirebaseAuthUser) {
                firebaseAuthUserPrincipal = principal as FirebaseAuthUser
            }
            val phoneNumber = firebaseAuthUserPrincipal?.getPhoneNumber() ?: ""
            val uid = firebaseAuthUserPrincipal?.getUid() ?: ""

            val userOptional = it.findById("+919742097429")
            val user: User = if (userOptional.isPresent) {
                val newUser = userOptional.get()
                newUser.fullName = "Updated Name -> Lokesh Basu"
                newUser
            } else {
                val newUser = User()
                newUser.id = phoneNumber
                newUser.fullName = phoneNumber
                newUser.gender = Gender.MALE
                newUser.uid = uid
                newUser
            }
            userRepository?.save(user)
        }
        return "Greetings from Public endpoint! $id"
    }

}
