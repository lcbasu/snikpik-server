package com.server.common.config

import com.amazonaws.services.sqs.AmazonSQSAsync
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.cloud.aws.messaging.config.QueueMessageHandlerFactory
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.converter.MappingJackson2MessageConverter
import org.springframework.messaging.converter.MessageConverter
import org.springframework.messaging.handler.annotation.support.PayloadMethodArgumentResolver

@Configuration
class AwsSQSConfiguration {

    @Bean
    fun queueMessagingTemplate(amazonSQSAsync: AmazonSQSAsync): QueueMessagingTemplate {
        return QueueMessagingTemplate(amazonSQSAsync)
    }

    @Bean
    fun queueMessageHandlerFactory(
        mapper: ObjectMapper?,
        amazonSQSAsync: AmazonSQSAsync?
    ): QueueMessageHandlerFactory? {
        val queueHandlerFactory = QueueMessageHandlerFactory()
        queueHandlerFactory.setAmazonSqs(amazonSQSAsync)
        queueHandlerFactory.setArgumentResolvers(
            listOf(PayloadMethodArgumentResolver(jackson2MessageConverter(mapper)))
        )
        return queueHandlerFactory
    }

    fun jackson2MessageConverter(mapper: ObjectMapper?): MessageConverter {
        val converter = MappingJackson2MessageConverter()
        // set strict content type match to false to enable the listener to handle AWS events
        converter.isStrictContentTypeMatch = false
        converter.objectMapper = mapper!!
        return converter
    }

}
