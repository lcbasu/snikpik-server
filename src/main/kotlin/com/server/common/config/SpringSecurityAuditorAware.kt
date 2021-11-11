package com.server.common.config

import com.server.common.model.UserDetailsFromToken
import com.server.common.utils.CommonUtils
import org.springframework.data.domain.AuditorAware
import org.springframework.security.core.context.SecurityContextHolder
import java.util.*

class SpringSecurityAuditorAware : AuditorAware<String> {
    override fun getCurrentAuditor(): Optional<String> {
        try {
            var userDetailsFromTokenPrincipal: UserDetailsFromToken? = null
            val securityContext = SecurityContextHolder.getContext()
            val principal = securityContext.authentication.principal
            if (principal is UserDetailsFromToken) {
                userDetailsFromTokenPrincipal = principal as UserDetailsFromToken
            }
            if (userDetailsFromTokenPrincipal == null) {
                return Optional.of("")
            }
            val absoluteMobile = userDetailsFromTokenPrincipal.getAbsoluteMobileNumber() ?: "NO_PHONE_NUMBER"
            val uid = userDetailsFromTokenPrincipal.getUid() ?: "NO_UID"
            return Optional.of(absoluteMobile + CommonUtils.STRING_SEPARATOR + uid)
        } catch (e: Exception) {
            return Optional.of("Un Authenticated Auditor")
        }
    }
}
