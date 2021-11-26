package com.server.ud.entities.post

import com.server.ud.enums.NotificationEntityType
import com.server.ud.enums.NotificationType
import org.springframework.data.cassandra.core.mapping.Table

@Table("notifications")
class Notifications (
    var notificationType: NotificationType,
    var notificationEntityId: String,
    var notificationEntityType: NotificationEntityType,
)
