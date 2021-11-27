package com.server.ud.entities.post

import com.server.ud.enums.NotificationEntityType
import com.server.ud.enums.NotificationType
import org.springframework.data.cassandra.core.mapping.Table
import java.time.Instant

//@Table("notifications")
class Notifications (
    val userId: String,
    val createdAt: Instant,
    val notificationType: NotificationType,
    val notificationEntityId: String,
    val notificationEntityType: NotificationEntityType,
)
