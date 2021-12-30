package com.server.ud.entities.user_activity

import com.server.ud.enums.UserChatMessageStatus
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

@Table("user_chat_message_status_by_user")
class UserChatsMessageStatusByUserId (

    @PrimaryKeyColumn(name = "user_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var userId: String,

    @PrimaryKeyColumn(name = "chat_id", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    var chatId: String,

    @PrimaryKeyColumn(name = "message_id", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
    var messageId: String,

    @Column("chat_message_status")
    var chatMessageStatus: UserChatMessageStatus,
)
