package com.server.ud.entities.user_activity

import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.CassandraType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

@Table("user_chat_messages_count")
class UserChatMessagesCount {

    @PrimaryKeyColumn(name = "chat_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var chatId: String? = null

    @Column("messages_count")
    @CassandraType(type = CassandraType.Name.COUNTER)
    var messagesCount: Long? = null

}
