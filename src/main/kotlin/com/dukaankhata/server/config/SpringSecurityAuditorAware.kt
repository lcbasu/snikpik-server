package com.dukaankhata.server.config

import com.dukaankhata.server.model.FirebaseAuthUser
import com.dukaankhata.server.utils.CommonUtils
import org.springframework.data.domain.AuditorAware
import org.springframework.security.core.context.SecurityContextHolder
import java.util.*

internal class SpringSecurityAuditorAware : AuditorAware<String> {
    override fun getCurrentAuditor(): Optional<String> {
        var firebaseAuthUserPrincipal: FirebaseAuthUser? = null
        val securityContext = SecurityContextHolder.getContext()
        val principal = securityContext.authentication.principal
        if (principal is FirebaseAuthUser) {
            firebaseAuthUserPrincipal = principal as FirebaseAuthUser
        }
        if (firebaseAuthUserPrincipal == null) {
            return Optional.of("")
        }
        val phoneNumber = firebaseAuthUserPrincipal.getPhoneNumber() ?: ""
        val uid = firebaseAuthUserPrincipal.getUid() ?: ""
        return Optional.of(phoneNumber + CommonUtils.STRING_SEPARATOR + uid)
    }
}
