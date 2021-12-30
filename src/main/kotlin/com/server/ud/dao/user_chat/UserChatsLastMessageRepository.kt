package com.server.ud.dao.user_chat

import com.server.ud.entities.user_activity.UserChatsLastMessage
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface UserChatsLastMessageRepository : CassandraRepository<UserChatsLastMessage?, String?> {
    fun findAllByChatId(chatId: String): List<UserChatsLastMessage>
}
