package com.server.ud.provider.social

import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class SocialRelationProcessingProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    fun processSocialRelation(fromUserId: String, toUserId: String) {
        runBlocking {
            logger.info("Do social relationship processing for fromUserId: $fromUserId & toUserId: $toUserId")
        }
    }


}
