package com.server.ud.service.queue

import com.amazonaws.services.sqs.model.SendMessageRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate
import org.springframework.stereotype.Service

// https://reflectoring.io/spring-cloud-aws-sqs/
@Service
class Producer {

    private val logger = LoggerFactory.getLogger(Producer::class.java)

    private val QUEUE_NAME = "https://sqs.ap-south-1.amazonaws.com/413074202297/ud-deffered-task-processing_standard"

    @Autowired
    private lateinit var messagingTemplate: QueueMessagingTemplate

    // TODO: Fix this as the fifo queue is not working
    fun sendToFifoQueue(messagePayload: String, messageGroupID: String, messageDedupID: String) {
        val sendMessageRequest = SendMessageRequest()
            .withQueueUrl(QUEUE_NAME)
            .withMessageBody(messagePayload)
            .withMessageGroupId(messageGroupID) // message group Id
            .withMessageDeduplicationId(messageDedupID) // deduplication Id
            .withDelaySeconds(5)
        messagingTemplate.convertAndSend(QUEUE_NAME, sendMessageRequest)
        logger.info("message sent")
    }
}
