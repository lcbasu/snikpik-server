package com.server.ud.provider.notification

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import com.server.common.enums.NotificationTokenProvider
import com.server.common.model.getMediaUrlForNotification
import com.server.ud.entities.user_activity.UserActivity
import com.server.ud.enums.UserActivityType
import com.server.ud.enums.UserAggregateActivityType
import com.server.ud.provider.automation.AutomationProvider
import com.server.ud.provider.user.UserV2Provider
import com.server.ud.utils.UDCommonUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component


@Component
class DeviceNotificationProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var userV2Provider: UserV2Provider

    @Autowired
    private lateinit var automationProvider: AutomationProvider

    fun sendNotification(userActivity: UserActivity) {
        try {
            if (userActivity.forUserId == null) {
                logger.error("User id for the activityId: ${userActivity.userActivityId} is null. Hence can not send notification.")
                return
            }

            val forUser = userV2Provider.getUser(userActivity.forUserId) ?: error("User not found for forUserId: ${userActivity.forUserId} for activityId: ${userActivity.userActivityId}")
            val byUser = userV2Provider.getUser(userActivity.byUserId) ?: error("User not found for byUserId: ${userActivity.byUserId} for activityId: ${userActivity.userActivityId}")

            logger.info("Sending notification sent to: " +
                    "userId: ${forUser.userId} for " +
                    "userActivityId: ${userActivity.userActivityId}.")

            val registrationToken = forUser.notificationToken

            val dataKey1 = "userActivityType"
            val dataValue1 = userActivity.userActivityType.name
            val dataKey2 = "userAggregateActivityType"
            val dataValue2 = userActivity.userAggregateActivityType.name
            val dataKey3 = "id"

            var title = ""
            var body = ""
            var mediaURL = ""
            var dataValue3 = userActivity.userActivityId

            var landingUrl = ""

            when (userActivity.userAggregateActivityType) {
                UserAggregateActivityType.NEW_POST_CREATED -> {
                    dataValue3 = userActivity.postId ?: error("Missing post id for activityId: ${userActivity.userActivityId}")
                    mediaURL = getMediaUrlForNotification(userActivity.postMediaDetails)
                    when (userActivity.userActivityType) {
                        UserActivityType.POST_CREATED -> {
                            title = "You have a new story post"
                            body = "Checkout the new story post by " + ((if (UDCommonUtils.isValidString(byUser.handle)) byUser.handle else byUser.fullName))
                        }
                        UserActivityType.WALL_CREATED -> {
                            title = "You have a new post on forum"
                            body = "Checkout the new question post by ${((if (UDCommonUtils.isValidString(byUser.handle)) byUser.handle else byUser.fullName))}, and comment to show your response."
                        }
                        else -> error("Invalid userActivityType: ${userActivity.userActivityType} for userActivityId: ${userActivity.userActivityId}")
                    }

                    landingUrl = "${UDCommonUtils.UNBOX_ROOT_URL}/posts/${dataValue3}"
                }
                UserAggregateActivityType.LIKED -> {
                    // Go to post only in case you like post, or comment, or reply
                    dataValue3 = userActivity.postId ?: error("Missing post id for activityId: ${userActivity.userActivityId}")
                    when (userActivity.userActivityType) {
                        UserActivityType.POST_LIKED -> {
                            title = "Your story post has a new like"
                            body = "${((if (UDCommonUtils.isValidString(byUser.handle)) byUser.handle else byUser.fullName))} liked your post."
                            mediaURL = getMediaUrlForNotification(userActivity.postMediaDetails)
                        }
                        UserActivityType.WALL_LIKED -> {
                            title = "Your forum post has a new like"
                            body = "${((if (UDCommonUtils.isValidString(byUser.handle)) byUser.handle else byUser.fullName))} liked your forum post."
                            mediaURL = getMediaUrlForNotification(userActivity.postMediaDetails)
                        }
                        UserActivityType.POST_COMMENT_LIKED,
                        UserActivityType.WALL_COMMENT_LIKED-> {
                            title = "Your comment has a new like"
                            body = "${((if (UDCommonUtils.isValidString(byUser.handle)) byUser.handle else byUser.fullName))} liked your comment: ${userActivity.commentText}."
                            mediaURL = getMediaUrlForNotification(userActivity.commentMediaDetails ?: userActivity.postMediaDetails)
                        }
                        UserActivityType.POST_COMMENT_REPLY_LIKED,
                        UserActivityType.WALL_COMMENT_REPLY_LIKED -> {
                            title = "Your reply has a new like"
                            body = "${((if (UDCommonUtils.isValidString(byUser.handle)) byUser.handle else byUser.fullName))} liked your reply: ${userActivity.replyText}."
                            mediaURL = getMediaUrlForNotification(userActivity.replyMediaDetails ?: userActivity.commentMediaDetails ?: userActivity.postMediaDetails)
                        }
                        else -> error("Invalid userActivityType: ${userActivity.userActivityType} for userActivityId: ${userActivity.userActivityId}")
                    }
                    landingUrl = "${UDCommonUtils.UNBOX_ROOT_URL}/posts/${dataValue3}"
                }
                UserAggregateActivityType.SAVED -> {
                    dataValue3 = userActivity.postId ?: error("Missing post id for activityId: ${userActivity.userActivityId}")
                    mediaURL = getMediaUrlForNotification(userActivity.postMediaDetails)
                    when (userActivity.userActivityType) {
                        UserActivityType.POST_SAVED -> {
                            title = "Your story post was saved"
                            body = "${((if (UDCommonUtils.isValidString(byUser.handle)) byUser.handle else byUser.fullName))} saved your story post."
                        }
                        UserActivityType.WALL_SAVED -> {
                            title = "Your forum post was saved"
                            body = "${((if (UDCommonUtils.isValidString(byUser.handle)) byUser.handle else byUser.fullName))} saved your forum post."
                        }
                        else -> error("Invalid userActivityType: ${userActivity.userActivityType} for userActivityId: ${userActivity.userActivityId}")
                    }
                    landingUrl = "${UDCommonUtils.UNBOX_ROOT_URL}/posts/${dataValue3}"
                }
                UserAggregateActivityType.SHARED -> {
                    when (userActivity.userActivityType) {
                        UserActivityType.POST_SHARED -> {
                            title = "Your story post was shared"
                            body = "${((if (UDCommonUtils.isValidString(byUser.handle)) byUser.handle else byUser.fullName))} shared your story post."
                            mediaURL = getMediaUrlForNotification(userActivity.postMediaDetails)
                            dataValue3 = userActivity.postId ?: error("Missing post id for activityId: ${userActivity.userActivityId}")
                        }
                        UserActivityType.WALL_SHARED -> {
                            title = "Your forum post was shared"
                            body = "${((if (UDCommonUtils.isValidString(byUser.handle)) byUser.handle else byUser.fullName))} shared your forum post."
                            mediaURL = getMediaUrlForNotification(userActivity.postMediaDetails)
                            dataValue3 = userActivity.postId ?: error("Missing post id for activityId: ${userActivity.userActivityId}")
                        }
                        UserActivityType.USER_PROFILE_SHARED -> {
                            title = "Your profile was shared"
                            body = "${((if (UDCommonUtils.isValidString(byUser.handle)) byUser.handle else byUser.fullName))} shared your profile."
                            mediaURL = getMediaUrlForNotification(byUser.dp)
                            dataValue3 = byUser.userId
                        }
                        else -> error("Invalid userActivityType: ${userActivity.userActivityType} for userActivityId: ${userActivity.userActivityId}")
                    }
                    landingUrl = "${UDCommonUtils.UNBOX_ROOT_URL}/posts/${dataValue3}"
                }
                UserAggregateActivityType.COMMENTED -> {
                    dataValue3 = userActivity.postId ?: error("Missing post id for activityId: ${userActivity.userActivityId}")
                    mediaURL = getMediaUrlForNotification(userActivity.commentMediaDetails ?: userActivity.postMediaDetails)
                    when (userActivity.userActivityType) {
                        UserActivityType.COMMENTED_ON_POST -> {
                            title = "Your post has a new comment"
                            body = "${((if (UDCommonUtils.isValidString(byUser.handle)) byUser.handle else byUser.fullName))} commented on your story post: ${userActivity.commentText}"
                        }
                        UserActivityType.COMMENTED_ON_WALL -> {
                            title = "Comment on your forum post"
                            body = "${((if (UDCommonUtils.isValidString(byUser.handle)) byUser.handle else byUser.fullName))} commented on your forum post: ${userActivity.commentText}"
                        }
                        else -> error("Invalid userActivityType: ${userActivity.userActivityType} for userActivityId: ${userActivity.userActivityId}")
                    }
                }
                UserAggregateActivityType.REPLIED -> {
                    dataValue3 = userActivity.postId ?: error("Missing post id for activityId: ${userActivity.userActivityId}")
                    mediaURL = getMediaUrlForNotification(userActivity.replyMediaDetails ?: userActivity.commentMediaDetails ?: userActivity.postMediaDetails)
                    when (userActivity.userActivityType) {
                        UserActivityType.REPLIED_TO_POST_COMMENT -> {
                            title = "Your comment has a new reply"
                            body = "${(if (UDCommonUtils.isValidString(byUser.handle)) byUser.handle else byUser.fullName)} replied on your comment in story post: ${userActivity.replyText}"
                        }
                        UserActivityType.REPLIED_TO_WALL_COMMENT -> {
                            title = "Your comment has a new reply"
                            body = "${(if (UDCommonUtils.isValidString(byUser.handle)) byUser.handle else byUser.fullName)} replied on your comment in forum post: ${userActivity.replyText}"
                        }
                        else -> error("Invalid userActivityType: ${userActivity.userActivityType} for userActivityId: ${userActivity.userActivityId}")
                    }
                    landingUrl = "${UDCommonUtils.UNBOX_ROOT_URL}/posts/${dataValue3}"
                }
                UserAggregateActivityType.FOLLOWED -> {
                    dataValue3 = byUser.userId
                    mediaURL = getMediaUrlForNotification(byUser.dp)
                    when (userActivity.userActivityType) {
                        UserActivityType.USER_FOLLOWED -> {
                            title = "You have a new follower"
                            body = "${(if (UDCommonUtils.isValidString(byUser.handle)) byUser.handle else byUser.fullName)} followed you."
                        }
                        else -> error("Invalid userActivityType: ${userActivity.userActivityType} for userActivityId: ${userActivity.userActivityId}")
                    }
                    landingUrl = "${UDCommonUtils.UNBOX_ROOT_URL}/users/${dataValue3}"
                }
                UserAggregateActivityType.CLICKED_CONNECT -> {
                    dataValue3 = byUser.userId
                    mediaURL = getMediaUrlForNotification(byUser.dp)
                    when (userActivity.userActivityType) {
                        UserActivityType.USER_CLICKED_CONNECT -> {
                            title = "You were reached out to"
                            body = "${(if (UDCommonUtils.isValidString(byUser.handle)) byUser.handle else byUser.fullName)} tried reaching out to you through your contact details."
                        }
                        else -> error("Invalid userActivityType: ${userActivity.userActivityType} for userActivityId: ${userActivity.userActivityId}")
                    }
                    landingUrl = "${UDCommonUtils.UNBOX_ROOT_URL}/users/${dataValue3}"
                }
                UserAggregateActivityType.MESSAGE_SENT_OR_RECEIVED -> {
                    dataValue3 = userActivity.chatId ?: error("Chat id is missing for activityId: ${userActivity.userActivityId}")
                    mediaURL = getMediaUrlForNotification(userActivity.chatMedia ?: byUser.dp)
                    when (userActivity.userActivityType) {
                        UserActivityType.USER_SENT_CHAT_MESSAGE -> {
                            title = "You have a new message"
                            body = "${(if (UDCommonUtils.isValidString(byUser.handle)) byUser.handle else byUser.fullName)} sent you a chat message: ${userActivity.chatText}"
                        }
                        else -> error("Invalid userActivityType: ${userActivity.userActivityType} for userActivityId: ${userActivity.userActivityId}")
                    }
                }
            }

            automationProvider.sendSlackMessageForUserActivity(
                title = title,
                body = body,
                mediaURL = mediaURL,
                landingUrl = landingUrl,
                userActivity = userActivity,
            )

            // See documentation on defining a message payload.
            if (forUser.notificationTokenProvider == NotificationTokenProvider.FIREBASE) {
                val message = Message
                    .builder()
                    .setNotification(
                        Notification
                            .builder()
                            .setTitle(title)
                            .setBody(body)
                            .setImage(mediaURL)
                            .build())
                    .putData(dataKey1, dataValue1)
                    .putData(dataKey2, dataValue2)
                    .putData(dataKey3, dataValue3)
                    .setToken(registrationToken)
                    .build()
                val response = FirebaseMessaging.getInstance().send(message)
                logger.info("Notification sent to: " +
                        "userId: ${forUser.userId} for " +
                        "userActivityId: ${userActivity.userActivityId} with " +
                        "response: $response")
            } else {
                error("Notification was not sent as the provider is not implemented or the token is null for: " +
                        "userId: ${forUser.userId} for " +
                        "userActivityId: ${userActivity.userActivityId}.")
            }
        } catch (e: Exception) {
            logger.error("Error while sending notification for userActivityId: ${userActivity.userActivityId}")
            e.printStackTrace()
        }
    }
}
