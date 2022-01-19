package com.server.common.provider.communication

import com.messagebird.MessageBirdClient
import com.messagebird.objects.Message
import io.ably.lib.rest.AblyRest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class CommunicationProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var messageBirdClient: MessageBirdClient

    fun sendSMS(phoneNumber: String, messageStr: String) {
        logger.info("Sending SMS to $phoneNumber with message $messageStr")

        val message = Message(
            "TestMessage",
            messageStr,
            phoneNumber
        )
        val response = messageBirdClient.sendMessage(message)

        logger.info("SMS sent successfully with response ${response.toString()}")
    }

}
