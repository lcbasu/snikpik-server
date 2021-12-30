package com.server.ud.entities.user_activity

import com.server.common.utils.DateUtils
import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.time.Instant

@Table("user_chats_by_user")
class UserChatsByUserId (

    @PrimaryKeyColumn(name = "user_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var userId: String,

    @PrimaryKeyColumn(name = "chat_id", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    var chatId: String,

    @PrimaryKeyColumn(name = "updated_at", ordinal = 2, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    var updatedAt: Instant = DateUtils.getInstantNow(),

)
