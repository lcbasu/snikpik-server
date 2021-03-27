package com.dukaankhata.server.security

import com.dukaankhata.server.model.FirebaseAuthUser
import io.sentry.protocol.User
import io.sentry.spring.SentryUserProvider
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class CustomUserProvider : SentryUserProvider {
    override fun provideUser(): User? {
        var firebaseAuthUserPrincipal: FirebaseAuthUser? = null
        val securityContext = SecurityContextHolder.getContext()
        val principal = securityContext.authentication.principal
        if (principal is FirebaseAuthUser) {
            firebaseAuthUserPrincipal = principal as FirebaseAuthUser
        }
        val user = User()
        if (firebaseAuthUserPrincipal == null) {
            user.username = "NOT_DEFINED"
            return user
        }
        val phoneNumber = firebaseAuthUserPrincipal.getPhoneNumber() ?: ""
        val uid = firebaseAuthUserPrincipal.getUid() ?: ""
        user.username = phoneNumber
        user.id = uid
        return user
    }
}
