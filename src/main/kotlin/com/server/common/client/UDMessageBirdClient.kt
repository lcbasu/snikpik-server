package com.server.common.client

import com.messagebird.MessageBirdClient
import com.messagebird.MessageBirdService
import com.messagebird.MessageBirdServiceImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class UDMessageBirdClient {

    @Bean
    fun getUDMessageBirdClient(): MessageBirdClient {
        // First create your service object
        val wsr: MessageBirdService = MessageBirdServiceImpl("fPbW7SP1EQqHp5mGmes9YQx7E")
        return MessageBirdClient(wsr)
    }

}
