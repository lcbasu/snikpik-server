package com.server.common.security

import com.server.common.model.FirebaseAuthUser
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
        val absoluteMobile = firebaseAuthUserPrincipal.getAbsoluteMobileNumber() ?: ""
        val uid = firebaseAuthUserPrincipal.getUid() ?: ""
        user.username = absoluteMobile
        user.id = uid
        return user
    }
}
