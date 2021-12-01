package com.server.ud.service.queue

import com.amazonaws.services.sqs.model.SendMessageRequest
import com.server.common.properties.AwsProperties
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate
import org.springframework.stereotype.Service

// https://reflectoring.io/spring-cloud-aws-sqs/
@Service
class Producer {

    private val logger = LoggerFactory.getLogger(Producer::class.java)

    @Autowired
    private lateinit var awsProperties: AwsProperties

    @Autowired
    private lateinit var messagingTemplate: QueueMessagingTemplate

    // TODO: Fix this as the fifo queue is not working
    fun sendToFifoQueue(messagePayload: String, messageGroupID: String, messageDedupID: String) {
        val sendMessageRequest = SendMessageRequest()
            .withQueueUrl(awsProperties.sqs.queueUrl)
            .withMessageBody(messagePayload)
            .withMessageGroupId(messageGroupID) // message group Id
            .withMessageDeduplicationId(messageDedupID) // deduplication Id
            .withDelaySeconds(5)
        messagingTemplate.convertAndSend(awsProperties.sqs.queueUrl, sendMessageRequest)
        logger.info("SQS:Message sent for processing: messagePayload: $messagePayload, messageGroupID: $messageGroupID, and messageDedupID: $messageDedupID")
    }
}
