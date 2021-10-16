package com.server.dk.config

import com.server.dk.model.FirebaseAuthUser
import com.server.dk.utils.CommonUtils
import org.springframework.data.domain.AuditorAware
import org.springframework.security.core.context.SecurityContextHolder
import java.util.*

class SpringSecurityAuditorAware : AuditorAware<String> {
    override fun getCurrentAuditor(): Optional<String> {
        try {
            var firebaseAuthUserPrincipal: FirebaseAuthUser? = null
            val securityContext = SecurityContextHolder.getContext()
            val principal = securityContext.authentication.principal
            if (principal is FirebaseAuthUser) {
                firebaseAuthUserPrincipal = principal as FirebaseAuthUser
            }
            if (firebaseAuthUserPrincipal == null) {
                return Optional.of("")
            }
            val absoluteMobile = firebaseAuthUserPrincipal.getAbsoluteMobileNumber() ?: "NO_PHONE_NUMBER"
            val uid = firebaseAuthUserPrincipal.getUid() ?: "NO_UID"
            return Optional.of(absoluteMobile + CommonUtils.STRING_SEPARATOR + uid)
        } catch (e: Exception) {
            return Optional.of("Un Authenticated Auditor")
        }
    }
}
