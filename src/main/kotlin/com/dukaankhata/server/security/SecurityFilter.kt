package com.dukaankhata.server.security

import com.dukaankhata.server.enums.CredentialType
import com.dukaankhata.server.model.Credentials
import com.dukaankhata.server.model.FirebaseAuthUser
import com.dukaankhata.server.properties.SecurityProperties
import com.dukaankhata.server.service.SecurityService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseToken
import com.nimbusds.jose.proc.SecurityContext
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor
import io.sentry.Sentry
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
class SecurityFilter(val processor: ConfigurableJWTProcessor<SecurityContext>) : OncePerRequestFilter() {

    val issuerUrl = "https://cognito-idp.ap-south-1.amazonaws.com/ap-south-1_RKZFH4zFL"

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
        if (securityService?.isPublic() == false) {
            verifyToken(request)
        }
        filterChain.doFilter(request, response)
    }

    private fun verifyToken(request: HttpServletRequest) {
        logger.info("Verifying token for request: ${request.toString()}")
        var session: String? = null
        var decodedToken: Any? = null
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
                    // Option 1: Try Firebase
                    try {
                        decodedToken = FirebaseAuth.getInstance().verifyIdToken(token)
                        type = CredentialType.ID_TOKEN_FIREBASE
                    } catch (e: Exception) {
                        logger.info("Token is not from Firebase Auth. Trying Cognito Auth.")
                    }

                    // Option 2: Try Cognito
                    if (decodedToken == null || type == null) {
                        // Not handling it under Try catch as the Option 2 should always result in
                        // valid token otherwise it should fail

                        val claimsSet = processor.process(token, null)

                        /**To verify JWT claims:
                        1.Verify that the token is not expired.
                        2.The audience (aud) claim should match the app client ID created in the Amazon Cognito user pool.
                        3.The issuer (iss) claim should match your user pool. For example, a user pool created in the us-east-1 region will have an iss value of: https://cognito-idp.us-east-1.amazonaws.com/<userpoolID>.
                        4.Check the token_use claim.
                        5.If you are only accepting the access token in your web APIs, its value must be access.
                        6.If you are only using the ID token, its value must be id.
                        7.If you are using both ID and access tokens, the token_use claim must be either id or access.
                        8.You can now trust the claims inside the token.
                         */

                        if (!isIssuedCorrectly(claimsSet)) {
                            error("Issuer ${claimsSet.issuer} in JWT token doesn't match cognito idp $issuerUrl")
                        }

                        if (!isIdToken(claimsSet)) {
                            error("JWT Token doesn't seem to be an ID Token")
                        }

                        decodedToken = claimsSet
                        type = CredentialType.ID_TOKEN_COGNITO
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            logger.error("Token Verification Exception:: ${e.localizedMessage}")
            Sentry.captureException(e)
        }
        decodedToken?.let {
            val firebaseAuthUser = if (type == CredentialType.ID_TOKEN_FIREBASE && it is FirebaseToken) {
                FirebaseAuthUser(
                    uid = it.uid,
                    name = it.name,
                    phoneNumber = it.claims["phone_number"] as String?,
                    picture = it.picture,
                    issuer = it.issuer
                )
            } else if (type == CredentialType.ID_TOKEN_COGNITO && it is JWTClaimsSet) {
                FirebaseAuthUser(
                    uid = it.getStringClaim("sub"),
                    name = it.getStringClaim("name"),
                    phoneNumber = it.getStringClaim("phone_number"),
                    picture = null,
                    issuer = "AWS_COGNITO"
                )
            } else {
                error("Incorrect object type defined for principal")
            }
            val authentication = UsernamePasswordAuthenticationToken(
                firebaseAuthUser,
                Credentials(type, it, token, session),
                null)
            authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
            SecurityContextHolder.getContext().authentication = authentication
        }
    }

    /**
     * Method that validates if the tokenId is issued correctly.
     * @param claimsSet
     * @return boolean
     */
    private fun isIssuedCorrectly(claimsSet: JWTClaimsSet): Boolean {
        return claimsSet.issuer == issuerUrl
    }

    /**
     * Method that validates if the ID token is valid.
     * @param claimsSet
     * @return
     */
    private fun isIdToken(claimsSet: JWTClaimsSet): Boolean {
        return claimsSet.getClaim("token_use") == "id"
    }

}
