package com.server.ud.dao.user_chat

import com.server.ud.entities.user_activity.UserChatsMessageStatusByUserId
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface UserChatsMessageStatusByUserIdRepository : CassandraRepository<UserChatsMessageStatusByUserId?, String?> {
    fun findAllByUserIdAndChatIdAndMessageId(userId: String, chatId: String, messageId: String): List<UserChatsMessageStatusByUserId>
}
