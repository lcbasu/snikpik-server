package com.server.ud.provider.user

import com.server.ud.dao.user.HandlesByUserRepository
import com.server.ud.entities.user.HandlesByUser
import com.server.ud.entities.user.UserV2
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class HandlesByUserProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var handlesByUserRepository: HandlesByUserRepository

    fun save(userV2: UserV2): HandlesByUser? {
        try {
            if (userV2.handle == null) {
                logger.error("handle is required to save HandlesByUser for userId: ${userV2.userId}.")
                return null
            }
            val handlesByUser = HandlesByUser(
                userId = userV2.userId,
                handle = userV2.handle!!,
            )
            logger.info("Completed")
            return handlesByUserRepository.save(handlesByUser)
        } catch (e: Exception) {
            logger.error("Saving HandlesByUser filed for userId: ${userV2.userId}.")
            e.printStackTrace()
            return null
        }
    }
}
