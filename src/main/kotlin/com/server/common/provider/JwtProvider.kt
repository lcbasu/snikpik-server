package com.server.common.provider

import com.server.common.model.UserDetailsForToken
import com.server.common.model.UserDetailsFromUDTokens
import com.server.ud.provider.user.UserV2ByMobileNumberProvider
import com.server.ud.provider.user.UserV2Provider
import io.jsonwebtoken.Claims
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
    private lateinit var userV2Provider: UserV2Provider

    fun extractSubject(token: String?): String {
        return extractClaim(
            token
        ) { obj: Claims -> obj.subject }
    }

    fun extractKey(token: String, key: String): String {
        return extractClaim(
            token
        ) { obj: Claims -> obj[key] as String }
    }

    fun extractExpiration(token: String?): Date {
        return extractClaim(
            token
        ) { obj: Claims -> obj.expiration }
    }

    fun <T> extractClaim(token: String?, claimsResolver: Function<Claims, T>): T {
        val claims = extractAllClaims(token)
        return claimsResolver.apply(claims)
    }

    fun extractAllClaims(token: String?): Claims {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).body
    }

    fun isTokenExpired(token: String): Boolean {
        return extractExpiration(token).before(Date())
    }

    fun generateToken(userDetails: UserDetailsForToken): String? {
        val claims: MutableMap<String, Any?> = HashMap()
        claims["uid"] = userDetails.uid
        claims["absoluteMobile"] = userDetails.absoluteMobile
        return createToken(claims, userDetails.uid)
    }

    fun createToken(claims: Map<String, Any?>, uid: String?): String? {
        return Jwts.builder().setClaims(claims).setSubject(uid)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + 1000 * 60 * 60 * 500))
            .signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact()
    }

    fun validateToken(token: String): UserDetailsFromUDTokens? {
        try {
            val isExpired = isTokenExpired(token)
            if (isExpired) {
                error("token is expired. token: $token")
            }
            // validate if the username is present in our DB or not
            val absoluteMobile = extractKey(token, "absoluteMobile")
            val uid = extractKey(token, "uid")
            val subject = extractSubject(token)
            if (uid != subject) {
                error("Token subject does not match with uid. token: $token")
            }

            val userV2ByMobileNumber = userV2ByMobileNumberProvider.getUserV2ByMobileNumber(absoluteMobile) ?: error("user not found for absoluteMobile: $absoluteMobile")
            val userV2 = userV2Provider.getUser(uid) ?: error("user not found for uid: $uid")

            if (userV2ByMobileNumber.userId != userV2.userId) {
                error("Token absolute mobile does not match with the user id. token: $token")
            }
            return UserDetailsFromUDTokens(
                token = token,
                uid = uid,
                absoluteMobile = absoluteMobile,
            )
        } catch (e: Exception) {
            e.printStackTrace()
            logger.error("Error while token validation. token: $token")
            return null
        }
    }
}
