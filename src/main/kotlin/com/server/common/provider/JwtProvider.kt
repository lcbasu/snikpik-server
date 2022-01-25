package com.server.common.provider

import com.server.common.model.UserDetailsForToken
import com.server.common.model.UserDetailsFromUDTokens
import com.server.ud.provider.auth.ValidTokenProvider
import com.server.ud.provider.user.UserV2ByMobileNumberProvider
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*
import java.util.function.Function


@Component
class JwtProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)


    private val SECRET_KEY = "hdsj6suhdvfshdf7gsjd"

    @Autowired
    private lateinit var userV2ByMobileNumberProvider: UserV2ByMobileNumberProvider

    @Autowired
    private lateinit var validTokenProvider: ValidTokenProvider

    fun generateToken(userDetails: UserDetailsForToken): String {
        val claims: MutableMap<String, Any?> = HashMap()
        claims["uid"] = userDetails.uid
        claims["absoluteMobile"] = userDetails.absoluteMobile
        return createToken(claims, userDetails.uid)
    }

    fun validateToken(token: String): UserDetailsFromUDTokens? {
        try {
            val isExpired = isTokenExpired(token)
            if (isExpired) {
                error("token is expired. token: $token")
            }

            val validToken = validTokenProvider.getValidToken(token)

            if (validToken == null || !validToken.valid) {
                error("Token is not valid: $token")
            }

            return validateTokenForClaims(token)
        } catch (e: Exception) {
//            e.printStackTrace()
            logger.error("Error while token validation in validateToken. token: $token")
            return null
        }
    }

    fun validateTokenForClaims(token: String): UserDetailsFromUDTokens? {
        try {
            // validate if the username is present in our DB or not
            val absoluteMobile = extractKey(token, "absoluteMobile")
            val uid = extractKey(token, "uid")
            val subject = extractSubject(token)
            if (uid != subject) {
                error("Token subject does not match with uid. token: $token")
            }

            val userV2ByMobileNumber = userV2ByMobileNumberProvider.getUserV2ByMobileNumber(absoluteMobile) ?: error("user not found for absoluteMobile: $absoluteMobile")

            // Commenting this as the user might not have actually been saved into db while the auth token is created.
//            val userV2 = userV2Provider.getUser(uid) ?: error("user not found for uid: $uid")

            if (userV2ByMobileNumber.userId != uid) {
                error("Token absolute mobile does not match with the user id. token: $token")
            }
            return UserDetailsFromUDTokens(
                token = token,
                uid = uid,
                absoluteMobile = absoluteMobile,
            )
        } catch (e: Exception) {
//            e.printStackTrace()
            logger.error("Error while token validation in validateTokenForClaims. token: $token")
            return null
        }
    }

    private fun createToken(claims: Map<String, Any?>, uid: String?): String {
        return Jwts.builder().setClaims(claims).setSubject(uid)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + 1000 * 60 * 60 * 500)) // Let us expire token every 1 minute and test out the build
            .signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact()
    }

    private fun extractSubject(token: String?): String {
        return extractClaim(
            token
        ) { obj: Claims -> obj.subject }
    }

    private fun extractKey(token: String, key: String): String {
        return extractClaim(
            token
        ) { obj: Claims -> obj[key] as String }
    }

    private fun extractExpiration(token: String?): Date {
        return extractClaim(
            token
        ) { obj: Claims -> obj.expiration }
    }

    private fun <T> extractClaim(token: String?, claimsResolver: Function<Claims, T>): T {
        val claims = extractAllClaims(token)
        return claimsResolver.apply(claims)
    }

    private fun extractAllClaims(token: String?): Claims {
        try {
            return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).body
        } catch (e: ExpiredJwtException) {
            logger.error("token is expired: $token")
            e.printStackTrace()
            // Return the claims for cases when the request was meant for generating a refresh token
            return e.claims
        }
    }

    private fun isTokenExpired(token: String): Boolean {
        return extractExpiration(token).before(Date())
    }
}
