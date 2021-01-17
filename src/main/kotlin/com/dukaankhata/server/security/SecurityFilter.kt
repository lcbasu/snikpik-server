package com.dukaankhata.server.security

import com.dukaankhata.server.enums.CredentialType
import com.dukaankhata.server.model.Credentials
import com.dukaankhata.server.model.FirebaseAuthUser
import com.dukaankhata.server.properties.SecurityProperties
import com.dukaankhata.server.service.SecurityService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseToken
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class SecurityFilter : OncePerRequestFilter() {

    @Autowired
    var securityService: SecurityService? = null

    @Autowired
    var restSecProps: SecurityProperties? = null

    @Autowired
    var cookieUtils: CookieUtils? = null

    @Autowired
    var securityProps: SecurityProperties? = null

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        verifyToken(request)
        filterChain.doFilter(request, response)
    }

    private fun verifyToken(request: HttpServletRequest) {
        logger.info("Verifying token for request: ${request.toString()}")
        var session: String? = null
        var decodedToken: FirebaseToken? = null
        var type: CredentialType? = null
        val strictServerSessionEnabled: Boolean = securityProps?.firebaseProps?.enableStrictServerSession == true
        val sessionCookie: Cookie? = cookieUtils?.getCookie("session")
        val token: String? = securityService?.getBearerToken(request)
        logger.info("Token to verify: $token")
        try {
            if (sessionCookie != null) {
                session = sessionCookie.value
                decodedToken = securityProps?.firebaseProps?.enableCheckSessionRevoked?.let {
                    FirebaseAuth.getInstance().verifySessionCookie(session,
                            it)
                }
                type = CredentialType.SESSION
            } else if (!strictServerSessionEnabled) {
                if (token !== null && !token.equals("undefined", ignoreCase = true)) {
                    decodedToken = FirebaseAuth.getInstance().verifyIdToken(token)
                    type = CredentialType.ID_TOKEN
                }
            }
        } catch (e: FirebaseAuthException) {
            e.printStackTrace()
            logger.error("Firebase Exception:: ${e.localizedMessage}")
        }
        decodedToken?.let {
            val firebaseAuthUser = FirebaseAuthUser(
                    uid = it.uid,
                    name = it.name,
                    phoneNumber = it.claims["phone_number"] as String?,
                    picture = it.picture,
                    issuer = it.issuer
            )
            val authentication = UsernamePasswordAuthenticationToken(
                    firebaseAuthUser,
                    Credentials(type, decodedToken, token, session),
                    null)
            authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
            SecurityContextHolder.getContext().authentication = authentication
        }
    }
}
