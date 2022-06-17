package com.server.sp.provider.user

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class SpUserProcessingProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var spUserProvider: SpUserProvider

    @Autowired
    private lateinit var handlesBySpUserProvider: HandlesBySpUserProvider

    fun processSpUser(userId: String) {
        GlobalScope.launch {
            logger.info("Start: SpUser processing for userId: $userId")

            val user = spUserProvider.getSpUser(userId) ?: error("No user found for $userId while doing user processing.")

            val handlesBySpUserFuture = async {
                handlesBySpUserProvider.save(user)
            }

            handlesBySpUserFuture.await()

            logger.info("Done: SpUser processing for userId: $userId")
        }
    }

    fun reProcessSpUser(userId: String) {
        GlobalScope.launch {
            logger.info("Start: Delete user data for dependent information for userId: $userId")

            // Now Re-Process the user
            processSpUser(userId)

            logger.info("End: Delete user data for dependent information for userId: $userId")
        }
    }

}
