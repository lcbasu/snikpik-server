package com.server.ud.provider.user

import com.server.common.enums.ReadableIdPrefix
import com.server.common.provider.CommonProvider
import com.server.common.provider.UniqueIdProvider
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

    @Autowired
    private lateinit var uniqueIdProvider: UniqueIdProvider

    @Autowired
    private lateinit var commonProvider: CommonProvider

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
        return commonProvider.isUsernameHandleAvailable(handle) && getUsersByHandle(handle) == null
    }

    fun save(userV2: UserV2) : UsersByHandle? {
        try {
            val savedUsersByHandle = usersByHandleRepository.save(UsersByHandle(
                handle = userV2.handle!!,
                userId = userV2.userId
            ))
            // All username have to be unique across all the tables
            uniqueIdProvider.saveId(userV2.handle, ReadableIdPrefix.USR.name)
            logger.info("Saved UsersByHandle into cassandra for userId: ${savedUsersByHandle.userId}")
            return savedUsersByHandle
        } catch (e: Exception) {
            logger.error("Saving UsersByHandle into cassandra failed for userId: ${userV2.userId}")
            e.printStackTrace()
            return null
        }
    }
}
