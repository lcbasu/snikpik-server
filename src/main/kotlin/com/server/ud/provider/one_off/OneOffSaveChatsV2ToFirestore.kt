package com.server.ud.provider.one_off

import com.server.ud.provider.user_chat.UserChatProvider
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class OneOffSaveChatsV2ToFirestore {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var userChatProvider: UserChatProvider

    fun saveChatsV2ToFirestore() {
        logger.info("Start save chats v2 to firestore")
        userChatProvider.saveAllChatsToFirestore()
        logger.info("Finish save chats v2 to firestore")
    }

}
