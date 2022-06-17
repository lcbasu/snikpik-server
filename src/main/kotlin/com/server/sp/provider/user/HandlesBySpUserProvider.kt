package com.server.sp.provider.user

import com.server.sp.dao.user.HandlesBySpUserRepository
import com.server.sp.entities.user.HandlesBySpUser
import com.server.sp.entities.user.SpUser
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class HandlesBySpUserProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var handlesBySpUserRepository: HandlesBySpUserRepository

    fun save(spUser: SpUser): HandlesBySpUser? {
        try {
            if (spUser.handle == null) {
                logger.error("handle is required to save HandlesBySpUser for userId: ${spUser.userId}.")
                return null
            }
            val handlesByUser = HandlesBySpUser(
                userId = spUser.userId,
                handle = spUser.handle!!,
            )
            logger.info("HandlesBySpUser saved for userId: ${spUser.userId}")
            return handlesBySpUserRepository.save(handlesByUser)
        } catch (e: Exception) {
            logger.error("Saving HandlesBySpUser failed for userId: ${spUser.userId}.")
            e.printStackTrace()
            return null
        }
    }
}
