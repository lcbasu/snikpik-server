package com.server.ud.enums

// Type of entities that can be the source of origin for a notification
enum class NotificationEntityType {
    POST,
    POST_COMMENT,
    POST_COMMENT_REPLY,
    WALL,
    WALL_COMMENT,
    WALL_COMMENT_REPLY,
    USER,
}
