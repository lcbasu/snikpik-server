package com.dukaankhata.server.service.impl

import com.dukaankhata.server.model.Credentials
import com.dukaankhata.server.model.FirebaseAuthUser
import com.dukaankhata.server.properties.SecurityProperties
import com.dukaankhata.server.security.CookieUtils
import com.dukaankhata.server.service.SecurityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import javax.servlet.http.HttpServletRequest


@Service
class SecurityServiceImpl : SecurityService() {

    @Autowired
    var httpServletRequest: HttpServletRequest? = null

    @Autowired
    var cookieUtils: CookieUtils? = null

    @Autowired
    var securityProps: SecurityProperties? = null
    override fun user(): FirebaseAuthUser? {
        var firebaseAuthUserPrincipal: FirebaseAuthUser? = null
        val securityContext = SecurityContextHolder.getContext()
        val principal = securityContext.authentication.principal
        if (principal is FirebaseAuthUser) {
            firebaseAuthUserPrincipal = principal as FirebaseAuthUser
        }
        return firebaseAuthUserPrincipal
    }
    override fun credentials(): Credentials {
        val securityContext = SecurityContextHolder.getContext()
        return securityContext.authentication.credentials as Credentials
    }
    override fun isPublic(): Boolean =
            securityProps?.allowedPublicApis?.contains(httpServletRequest!!.requestURI) == true

    override fun getBearerToken(request: HttpServletRequest?): String {
        var bearerToken = ""
        val authorization = request?.getHeader("Authorization")
        if (StringUtils.hasText(authorization) && authorization?.startsWith("Bearer ") == true) {
            bearerToken = authorization.substring(7, authorization.length)
        }
        return bearerToken
    }
}
