package com.server.ud.provider.auth

import com.server.ud.dao.auth.ValidTokenRepository
import com.server.ud.entities.auth.ValidToken
import com.server.ud.utils.UDCommonUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ValidTokenProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var validTokenRepository: ValidTokenRepository

    fun getValidToken(token: String): ValidToken? =
        try {
            val hashedToken = UDCommonUtils.getSha256Hash(token)
            val users = validTokenRepository.findAllByToken(hashedToken)
            if (users.size > 1) {
                error("More than one ValidToken has same token: $token")
            }
            users.firstOrNull()
        } catch (e: Exception) {
            logger.error("Getting ValidToken for $token failed.")
            e.printStackTrace()
            null
        }

    fun saveValidToken(token: String, valid: Boolean, validByLoginSequenceId: String, invalidByLoginSequenceId: String? = null) : ValidToken? {
        try {
            // Not sent yet or already expired, so create a new one
            val hashedToken = UDCommonUtils.getSha256Hash(token)
            return validTokenRepository.save(ValidToken(
                token = hashedToken,
                valid = valid,
                validByLoginSequenceId = validByLoginSequenceId,
                invalidByLoginSequenceId = invalidByLoginSequenceId,
            ))
        } catch (e: Exception) {
            logger.error("Saving ValidToken for token: $token failed.")
            e.printStackTrace()
            return null
        }
    }

}
