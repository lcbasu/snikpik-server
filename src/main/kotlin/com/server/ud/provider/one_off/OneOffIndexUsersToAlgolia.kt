package com.server.ud.provider.one_off

import com.server.ud.provider.user.UserV2Provider
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class OneOffIndexUsersToAlgolia {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var userV2Provider: UserV2Provider

    fun saveUsersToAlgolia() {
        logger.info("Start save users to algolia")
        userV2Provider.saveAllToAlgolia()
        logger.info("Finish save users to algolia")
    }

}
