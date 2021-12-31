package com.server.ud.provider.notification

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import com.server.ud.dao.social.FollowersByUserRepository
import com.server.ud.entities.user.UserV2
import com.server.ud.entities.user_activity.UserActivity
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component


@Component
class DeviceNotificationProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var followersByUserRepository: FollowersByUserRepository

    fun sendNotification(userV2: UserV2, userActivity: UserActivity) {
        logger.info("Sending notification sent to: " +
                "userId: ${userV2.userId} for " +
                "userActivityId: ${userActivity.userActivityId}.")
        val registrationToken = userV2.notificationToken
        // See documentation on defining a message payload.


        val message = Message
            .builder()
            .setNotification(
                Notification
                    .builder()
                    .setTitle("userActivity.title")
                    .setBody("userActivity.description")
                    .setImage("")
                    .build())
            .putData("score", "850")
            .putData("time", "2:45")
            .setToken(registrationToken)
            .build()
        val response = FirebaseMessaging.getInstance().send(message)
        logger.info("Notification sent to: " +
                "userId: ${userV2.userId} for " +
                "userActivityId: ${userActivity.userActivityId} with " +
                "response: $response")
    }
}
