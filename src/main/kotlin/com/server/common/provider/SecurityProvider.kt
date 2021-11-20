package com.server.common.provider

import com.server.common.model.UserDetailsFromToken
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class SecurityProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    fun validateRequest(): UserDetailsFromToken {
        val user = getFirebaseAuthUser() ?: error("User not authenticated")
        logger.info("=== validateRequest ===")
        logger.info(user.toString())
        return user
    }

    fun getFirebaseAuthUser(): UserDetailsFromToken? {
        var userDetailsFromTokenPrincipal: UserDetailsFromToken? = null
        val securityContext = SecurityContextHolder.getContext()
        val principal = securityContext.authentication.principal
        if (principal is UserDetailsFromToken) {
            userDetailsFromTokenPrincipal = principal as UserDetailsFromToken
        }
        return userDetailsFromTokenPrincipal
    }

    private fun isAnonymous(): Boolean {
        return getFirebaseAuthUser() == null || getFirebaseAuthUser()?.getIsAnonymous() == true
    }
}
