package com.server.ud.entities.post

import com.server.ud.enums.NotificationEntityType
import com.server.ud.enums.NotificationType
import org.springframework.data.cassandra.core.mapping.Table
import java.time.Instant

//@Table("notifications_by_destination_user")
class NotificationsByDestinationUser (
    val userId: String,
    val notificationCreatedByUserId: String,
    val notificationCreatedAt: Instant,
    val notificationType: NotificationType,
    val notificationEntityId: String,
    val notificationEntityType: NotificationEntityType,
)
