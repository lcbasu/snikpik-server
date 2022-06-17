package com.server.common.provider.auth

import com.server.common.dao.auth.RefreshTokenRepository
import com.server.common.entities.auth.RefreshToken
import com.server.common.utils.CommonUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class RefreshTokenProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var refreshTokenRepository: RefreshTokenRepository

    fun getRefreshToken(loginSequenceId: String): RefreshToken? =
        try {
            val users = refreshTokenRepository.findAllByLoginSequenceId(loginSequenceId)
            if (users.size > 1) {
                error("More than one RefreshToken has same loginSequenceId: $loginSequenceId")
            }
            users.firstOrNull()
        } catch (e: Exception) {
            logger.error("Getting RefreshToken for $loginSequenceId failed.")
            e.printStackTrace()
            null
        }

    fun saveRefreshToken(loginSequenceId: String, userId: String, absoluteMobile: String, token: String, usedToRefresh: Boolean) : RefreshToken? {
        try {
            val hashedToken = CommonUtils.getSha256Hash(token)
            // Not sent yet or already expired, so create a new one
            return refreshTokenRepository.save(RefreshToken(
                loginSequenceId = loginSequenceId,
                absoluteMobile = absoluteMobile,
                userId = userId,
                token = hashedToken,
                usedToRefresh = usedToRefresh,
            ))
        } catch (e: Exception) {
            logger.error("Saving RefreshToken for loginSequenceId: $loginSequenceId failed.")
            e.printStackTrace()
            return null
        }
    }

}
