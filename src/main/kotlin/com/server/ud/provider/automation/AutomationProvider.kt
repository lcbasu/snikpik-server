package com.server.ud.provider.automation

import com.github.seratch.jslack.Slack
import com.github.seratch.jslack.api.webhook.Payload
import com.server.common.controller.Msg91SMSDeliveryObject
import com.server.common.properties.AutomationProperties
import com.server.ud.dto.PostReportRequest
import com.server.ud.dto.UserReportRequest
import com.server.ud.entities.auth.OtpValidation
import com.server.ud.entities.post.Post
import com.server.ud.entities.post.getCategories
import com.server.ud.entities.user.UserV2
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class AutomationProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var automationProperties: AutomationProperties

    fun sendTestSlackMessage() {
        try {

            val payload = Payload.builder().text("Test message").build()

            Slack.getInstance().send(automationProperties.slack.webhook.newUser, payload)

        } catch (e: Exception) {
            e.printStackTrace()
            logger.error("Error while sending test slack message", e)
        }
    }

    fun sendSlackMessageForNewUser(user: UserV2) {
        GlobalScope.launch {
            try {
                val message = "${if (user.anonymous) "Guest User" else "User with Phone Number"}. Details: ${user.absoluteMobile}, ${user.userId}, ${user.permanentLocationName}, ${user.permanentLocationZipcode}"
                val payload = Payload
                    .builder()
                    .text(message)
                    .build()

                Slack.getInstance().send(automationProperties.slack.webhook.newUser, payload)

            } catch (e: Exception) {
                e.printStackTrace()
                logger.error("sendSlackMessageForNewUser Error while sending test slack message", e)
            }
        }
    }

    fun sendSlackMessageForNewPost(post: Post) {
        GlobalScope.launch {
            try {
                val message = "New Post (${post.postType}) created with title: ${post.description ?: post.title} in categories: ${
                    post.getCategories().categories.joinToString(
                        ","
                    ) { it.displayName }
                } by ${post.userName}, ${post.userHandle}. Link: https://www.letsunbox.in/posts/${post.postId}"

                val payload = Payload.builder().text(message).build()
                Slack.getInstance().send(automationProperties.slack.webhook.newPost, payload)
            } catch (e: Exception) {
                e.printStackTrace()
                logger.error("sendSlackMessageForNewPost Error while sending test slack message", e)
            }
        }
    }

    fun sendSlackMessageForOTP(otpValidation: OtpValidation) {
        GlobalScope.launch {
            try {
                val message = "[DO NOT SHARE WITH ANYONE]. OTP for ${otpValidation.absoluteMobile} is ${otpValidation.otp}"
                val payload = Payload.builder().text(message).build()
                Slack.getInstance().send(automationProperties.slack.webhook.otpDelivery, payload)
            } catch (e: Exception) {
                e.printStackTrace()
                logger.error("sendSlackMessageForOTP Error while sending test slack message", e)
            }
        }
    }

    fun sendSlackMessageForOTPDelivery(msg91SMSDeliveryObject: Msg91SMSDeliveryObject) {
        GlobalScope.launch {
            try {
                val number = msg91SMSDeliveryObject.report?.firstOrNull()?.number
                val status = msg91SMSDeliveryObject.report?.firstOrNull()?.status
                val message = "SenderId: ${msg91SMSDeliveryObject.senderId}, requestId: ${msg91SMSDeliveryObject.requestId}, userId: ${msg91SMSDeliveryObject.userId}, campaignName: ${msg91SMSDeliveryObject.campaignName}. OTP Delivery for $number with status: $status"
                val payload = Payload.builder().text(message).build()
                Slack.getInstance().send(automationProperties.slack.webhook.otpDelivery, payload)
            } catch (e: Exception) {
                e.printStackTrace()
                logger.error("sendSlackMessageForOTPDelivery Error while sending test slack message", e)
            }
        }
    }

    fun sendSlackMessageForUserReport(request: UserReportRequest, reportedBy: UserV2, reportedFor: UserV2) {
        GlobalScope.launch {
            try {
                val message = "${reportedBy.userId} reported ${reportedFor.userId} for ${request.reason}."
                val payload = Payload.builder().text(message).build()
                Slack.getInstance().send(automationProperties.slack.webhook.userReport, payload)
            } catch (e: Exception) {
                e.printStackTrace()
                logger.error("sendSlackMessageForUserReport Error while sending test slack message", e)
            }
        }
    }

    fun sendSlackMessageForPostReport(request: PostReportRequest, reportedBy: UserV2, post: Post) {
        GlobalScope.launch {
            try {
                val message = "${reportedBy.userId} reported ${request.postId} for ${request.reason}. Post Details: (${post.postType}) title: ${post.description ?: post.title} in categories: ${
                    post.getCategories().categories.joinToString(
                        ","
                    ) { it.displayName }
                } by ${post.userName}, ${post.userHandle}. Link: https://www.letsunbox.in/posts/${post.postId}"


                val payload = Payload.builder().text(message).build()
                Slack.getInstance().send(automationProperties.slack.webhook.postReport, payload)
            } catch (e: Exception) {
                e.printStackTrace()
                logger.error("sendSlackMessageForUserReport Error while sending test slack message", e)
            }
        }
    }

}
