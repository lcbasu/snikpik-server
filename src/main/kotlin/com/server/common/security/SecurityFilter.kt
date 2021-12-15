package com.server.common.security

import com.server.common.enums.CredentialType
import com.server.common.model.Credentials
import com.server.common.model.UserDetailsFromToken
import com.server.common.properties.AwsProperties
import com.server.common.properties.SecurityProperties
import com.server.common.service.SecurityService
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

    @Autowired
    private lateinit var securityService: SecurityService

    @Autowired
    private lateinit var awsProperties: AwsProperties

    @Autowired
    private lateinit var cookieUtils: CookieUtils

    @Autowired
    private lateinit var securityProps: SecurityProperties

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
//        if (securityService?.isPublic() == false) {
//            verifyToken(request)
//        }
        verifyToken(request)
        filterChain.doFilter(request, response)
    }

    private fun verifyToken(request: HttpServletRequest) {
//        logger.info("Verifying token for request: ${request.toString()}")
        var session: String? = null
        var decodedToken: Any? = null
        var type: CredentialType? = null
        val strictServerSessionEnabled: Boolean = securityProps?.firebaseProps?.enableStrictServerSession == true
        val sessionCookie: Cookie? = cookieUtils?.getCookie("session")
        val token: String? = securityService?.getBearerToken(request)
//        logger.info("Token to verify: $token")
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
//                        logger.info("Token is not from Firebase Auth. Trying Cognito Auth.")
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

//                        if (!isIssuedCorrectly(claimsSet)) {
//                            error("Issuer ${claimsSet.issuer} in JWT token doesn't match cognito idp ${awsProperties.amplify.wellKnownIssuer}")
//                        }
//
//                        if (!isIdToken(claimsSet)) {
//                            error("JWT Token doesn't seem to be an ID Token")
//                        }

                        if (isIssuedCorrectly(claimsSet) && isIdToken(claimsSet)) {
                            decodedToken = claimsSet
                            type = CredentialType.ID_TOKEN_COGNITO
                        }
                    }
                }
            }
        } catch (e: Exception) {
//            e.printStackTrace()
//            logger.error("Token Verification Exception:: ${e.localizedMessage}")
        }
        decodedToken?.let {
            val userDetailsFromToken = if (type == CredentialType.ID_TOKEN_FIREBASE && it is FirebaseToken) {
                val phone = it.claims["phone_number"] as String?
                val email = it.email
                UserDetailsFromToken(
                    uid = it.uid,
                    name = it.name,
                    absoluteMobile = it.claims["phone_number"] as String?,
                    picture = it.picture,
                    issuer = it.issuer,
                    anonymous = phone == null && email == null,
                    email = email,
//                    handle = email?.let { it.substringBefore("@") }
                )
            } else if (type == CredentialType.ID_TOKEN_COGNITO && it is JWTClaimsSet) {
                UserDetailsFromToken(
                    uid = it.getStringClaim("sub"),
                    name = it.getStringClaim("name"),
                    absoluteMobile = it.getStringClaim("phone_number"),
                    picture = null,
                    issuer = "AWS_COGNITO",
                    anonymous = false,
                )
            } else {
                error("Incorrect object type defined for principal")
            }
            val authentication = UsernamePasswordAuthenticationToken(
                userDetailsFromToken,
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
        return claimsSet.issuer == awsProperties.amplify.wellKnownIssuer
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
