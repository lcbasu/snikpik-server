package com.server.ud.provider.user

import com.server.ud.dao.user.UsersByHandleRepository
import com.server.ud.entities.user.UserV2
import com.server.ud.entities.user.UsersByHandle
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class UsersByHandleProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    var MIN_HANDLE_LENGTH = 4

    @Autowired
    private lateinit var usersByHandleRepository: UsersByHandleRepository

    fun getUsersByHandle(handle: String): UsersByHandle? =
        try {
            val users = usersByHandleRepository.findAllByHandle(handle)
            if (users.size > 1) {
                error("More than one user has same handle: $handle")
            }
            users.firstOrNull()
        } catch (e: Exception) {
            logger.error("Getting UsersByHandle for $handle failed.")
            null
        }

    fun isHandleAvailable(handle: String): Boolean {
        if (handle.length < MIN_HANDLE_LENGTH) {
            return false
        }
        return getUsersByHandle(handle) == null
    }

    fun save(userV2: UserV2) : UsersByHandle? {
        try {
            val savedUsersByHandle = usersByHandleRepository.save(UsersByHandle(
                handle = userV2.handle!!,
                userId = userV2.userId
            ))
            logger.info("Saved UsersByHandle into cassandra for userId: ${savedUsersByHandle.userId}")
            logger.info("Completed")
            return savedUsersByHandle
        } catch (e: Exception) {
            logger.error("Saving UsersByHandle into cassandra failed for userId: ${userV2.userId}")
            e.printStackTrace()
            return null
        }
    }
}
