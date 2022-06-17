package com.server.common.provider.automation

import com.github.seratch.jslack.Slack
import com.github.seratch.jslack.api.webhook.Payload
import com.server.common.controller.Msg91SMSDeliveryObject
import com.server.common.properties.AutomationProperties
import com.server.shop.entities.UserV3
import com.server.sp.entities.user.SpUser
import com.server.ud.dto.PostReportRequest
import com.server.ud.dto.UserReportRequest
import com.server.common.entities.auth.OtpValidation
import com.server.ud.entities.post.Post
import com.server.ud.entities.post.getCategories
import com.server.ud.entities.user.UserV2
import com.server.ud.entities.user_activity.UserActivity
import com.server.ud.enums.UserAggregateActivityType
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

    final val divider = "-------------------------------------------------------------------------------"

    fun sendTestSlackMessage() {
        try {

            val payload = Payload.builder().text("Test message").build()

            Slack.getInstance().send(automationProperties.slack.webhook.newUser, payload)

        } catch (e: Exception) {
            e.printStackTrace()
            logger.error("Error while sending slack message", e)
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
                logger.error("sendSlackMessageForNewUser Error while sending slack message", e)
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
                } by ${post.userName}, ${post.userHandle} at ${post.locationName}, ${post.locality}, ${post.subLocality}, ${post.route}, ${post.city}, ${post.state}, ${post.country}, ${post.zipcode} . Link: https://www.letsunbox.in/posts/${post.postId}"

                val payload = Payload.builder().text("$divider\n$message\n$divider\n\n").build()
                Slack.getInstance().send(automationProperties.slack.webhook.newPost, payload)
            } catch (e: Exception) {
                e.printStackTrace()
                logger.error("sendSlackMessageForNewPost Error while sending slack message", e)
            }
        }
    }

    fun sendSlackMessageForPostDeletion(post: Post) {
        GlobalScope.launch {
            try {
                val message = "User requested to delete post: ${post.postId} (${post.postType}) created with title: ${post.description ?: post.title} in categories: ${
                    post.getCategories().categories.joinToString(
                        ","
                    ) { it.displayName }
                } by ${post.userName}, ${post.userHandle} at ${post.locationName}, ${post.locality}, ${post.subLocality}, ${post.route}, ${post.city}, ${post.state}, ${post.country}, ${post.zipcode} . Link: https://www.letsunbox.in/posts/${post.postId}"

                val payload = Payload.builder().text("$divider\n$message\n$divider\n\n").build()
                Slack.getInstance().send(automationProperties.slack.webhook.postDelete, payload)
            } catch (e: Exception) {
                e.printStackTrace()
                logger.error("sendSlackMessageForPostDeletion Error while sending slack message", e)
            }
        }
    }

    fun sendSlackMessageForOTP(otpValidation: OtpValidation) {
        GlobalScope.launch {
            try {
                val message = "[DO NOT SHARE WITH ANYONE]. OTP for ${otpValidation.absoluteMobile} is ${otpValidation.otp}"
                val payload = Payload.builder().text("$divider\n$message\n$divider\n\n").build()
                Slack.getInstance().send(automationProperties.slack.webhook.otpDelivery, payload)
            } catch (e: Exception) {
                e.printStackTrace()
                logger.error("sendSlackMessageForOTP Error while sending slack message", e)
            }
        }
    }

    fun sendSlackMessageForReSendingOfOTP(otpValidation: OtpValidation) {
        GlobalScope.launch {
            try {
                val message = "[DO NOT SHARE WITH ANYONE] [RE-SENT USING MESSAGE-BIRD]. OTP for ${otpValidation.absoluteMobile} is ${otpValidation.otp}"
                val payload = Payload.builder().text("$divider\n$message\n$divider\n\n").build()
                Slack.getInstance().send(automationProperties.slack.webhook.otpDelivery, payload)
            } catch (e: Exception) {
                e.printStackTrace()
                logger.error("sendSlackMessageForOTP Error while sending slack message", e)
            }
        }
    }


    fun sendSlackMessageForOTPDelivery(msg91SMSDeliveryObject: Msg91SMSDeliveryObject) {
        GlobalScope.launch {
            try {
                val number = msg91SMSDeliveryObject.report?.firstOrNull()?.number
                val status = msg91SMSDeliveryObject.report?.firstOrNull()?.status
                val message = "SenderId: ${msg91SMSDeliveryObject.senderId}, requestId: ${msg91SMSDeliveryObject.requestId}, userId: ${msg91SMSDeliveryObject.userId}, campaignName: ${msg91SMSDeliveryObject.campaignName}. OTP Delivery for $number with status: $status"
                val payload = Payload.builder().text("$divider\n$message\n$divider\n\n").build()
                Slack.getInstance().send(automationProperties.slack.webhook.otpDelivery, payload)
            } catch (e: Exception) {
                e.printStackTrace()
                logger.error("sendSlackMessageForOTPDelivery Error while sending slack message", e)
            }
        }
    }

    fun sendSlackMessageForUserReport(request: UserReportRequest, reportedBy: UserV2, reportedFor: UserV2) {
        GlobalScope.launch {
            try {
                val message = "ReportedBy: ${reportedBy.fullName}, ${reportedBy.handle}, ${reportedBy.absoluteMobile}, ${reportedBy.userId}, ${reportedBy.permanentLocationName}, ${reportedBy.permanentLocationZipcode}\n\n" +
                        "ReportedFor: ${reportedFor.fullName}, ${reportedFor.handle}, ${reportedFor.absoluteMobile}, ${reportedFor.userId}, ${reportedFor.permanentLocationName}, ${reportedFor.permanentLocationZipcode}\n\n" +
                        "Reason: ${request.reason}."
                val payload = Payload.builder().text("$divider\n$message\n$divider\n\n").build()
                Slack.getInstance().send(automationProperties.slack.webhook.userReport, payload)
            } catch (e: Exception) {
                e.printStackTrace()
                logger.error("sendSlackMessageForUserReport Error while sending slack message", e)
            }
        }
    }

    fun sendSlackMessageForPostReport(request: PostReportRequest, reportedBy: UserV2, post: Post) {
        GlobalScope.launch {
            try {
                val message = "ReportedBy: ${reportedBy.fullName}, ${reportedBy.handle}, ${reportedBy.absoluteMobile}, ${reportedBy.userId}, ${reportedBy.permanentLocationName}, ${reportedBy.permanentLocationZipcode}\n\n" +
                        "Reason: ${request.reason}\n\n" +
                        "Post (${post.postType}) Title: ${post.description ?: post.title} in categories: ${
                            post.getCategories().categories.joinToString(
                                ","
                            ) { it.displayName }
                        } by ${post.userName}, ${post.userHandle}. Link: https://www.letsunbox.in/posts/${post.postId}"

                val payload = Payload.builder().text("$divider\n$message\n$divider\n\n").build()
                Slack.getInstance().send(automationProperties.slack.webhook.postReport, payload)
            } catch (e: Exception) {
                e.printStackTrace()
                logger.error("sendSlackMessageForUserReport Error while sending slack message", e)
            }
        }
    }

    fun registerInterestForShopCategoryLaunch(userV3: UserV3) {
        GlobalScope.launch {
            try {
                val message = "Interested in Shop Category Launch: ${userV3.fullName}, ${userV3.handle}, ${userV3.absoluteMobile}, ${userV3.id}, ${userV3.permanentLocationName}, ${userV3.permanentLocationCity}, ${userV3.permanentLocationState}, ${userV3.permanentLocationZipcode}"
                val payload = Payload.builder().text("$divider\n$message\n$divider\n\n").build()
                Slack.getInstance().send(automationProperties.slack.webhook.shopLaunch, payload)
            } catch (e: Exception) {
                e.printStackTrace()
                logger.error("sendSlackMessageForUserReport Error while sending slack message", e)
            }
        }
    }

    fun sendSlackMessageForUserActivity(title: String, body: String, mediaURL: String, landingUrl: String, userActivity: UserActivity) {
        GlobalScope.launch {
            try {
                if (userActivity.userAggregateActivityType == UserAggregateActivityType.MESSAGE_SENT_OR_RECEIVED) {
                    return@launch
                }
                val message = "User Activity: ${userActivity.userActivityId}, ${userActivity.userActivityType}, ${userActivity.userAggregateActivityType}\n\n\n" +
                        "Title: $title\n\n" +
                        "Body: $body\n\n" +
//                        "Media URL: $mediaURL\n\n\n\n" +
                        "Landing URL: $landingUrl\n\n"
                val payload = Payload.builder().text("$divider\n$message\n$divider\n\n").build()
                Slack.getInstance().send(automationProperties.slack.webhook.userActivity, payload)
            } catch (e: Exception) {
                e.printStackTrace()
                logger.error("sendSlackMessageForUserActivity Error while sending slack message", e)
            }
        }
    }

    fun sendSlackMessageForNewSpUser(user: SpUser) {
        GlobalScope.launch {
            try {

                val message = "${if (user.anonymous) "Guest User" else "User with Phone Number"}. Details: ${user.absoluteMobile}, ${user.userId}"
                val payload = Payload
                    .builder()
                    .text(message)
                    .build()

                Slack.getInstance().send(automationProperties.slack.webhook.newUser, payload)

            } catch (e: Exception) {
                e.printStackTrace()
                logger.error("sendSlackMessageForNewUser Error while sending slack message", e)
            }
        }
    }

}
