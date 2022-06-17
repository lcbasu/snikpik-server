package com.server.sp.provider.user

import com.server.common.enums.ReadableIdPrefix
import com.server.common.provider.UniqueIdProvider
import com.server.sp.dao.user.SpUsersByHandleRepository
import com.server.sp.entities.user.SpUser
import com.server.sp.entities.user.SpUsersByHandle
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class SpUsersByHandleProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var uniqueIdProvider: UniqueIdProvider

    @Autowired
    private lateinit var spUsersByHandleRepository: SpUsersByHandleRepository

    fun getUsersByHandle(handle: String): SpUsersByHandle? =
        try {
            val users = spUsersByHandleRepository.findAllByHandle(handle)
            if (users.size > 1) {
                error("More than one user has same handle: $handle")
            }
            users.firstOrNull()
        } catch (e: Exception) {
            logger.error("Getting SpUsersByHandle for $handle failed.")
            null
        }

    fun isHandleAvailable(handle: String): Boolean {
        return getUsersByHandle(handle) == null
    }

    fun save(spUser: SpUser) : SpUsersByHandle? {
        try {
            val savedSpUsersByHandle = spUsersByHandleRepository.save(SpUsersByHandle(
                handle = spUser.handle!!,
                userId = spUser.userId
            ))
            // All username have to be unique across all the tables
            uniqueIdProvider.saveId(spUser.handle, ReadableIdPrefix.USR.name)
            logger.info("Saved SpUsersByHandle into cassandra for userId: ${savedSpUsersByHandle.userId}")
            return savedSpUsersByHandle
        } catch (e: Exception) {
            logger.error("Saving SpUsersByHandle into cassandra failed for userId: ${spUser.userId}")
            e.printStackTrace()
            return null
        }
    }
}
