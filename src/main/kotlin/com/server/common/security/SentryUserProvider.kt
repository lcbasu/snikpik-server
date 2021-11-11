package com.server.common.security

import com.server.common.model.UserDetailsFromToken
import io.sentry.protocol.User
import io.sentry.spring.SentryUserProvider
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class CustomUserProvider : SentryUserProvider {
    override fun provideUser(): User? {
        var userDetailsFromTokenPrincipal: UserDetailsFromToken? = null
        val securityContext = SecurityContextHolder.getContext()
        val principal = securityContext.authentication.principal
        if (principal is UserDetailsFromToken) {
            userDetailsFromTokenPrincipal = principal as UserDetailsFromToken
        }
        val user = User()
        if (userDetailsFromTokenPrincipal == null) {
            user.username = "NOT_DEFINED"
            return user
        }
        val absoluteMobile = userDetailsFromTokenPrincipal.getAbsoluteMobileNumber() ?: ""
        val uid = userDetailsFromTokenPrincipal.getUid() ?: ""
        user.username = absoluteMobile
        user.id = uid
        return user
    }
}
