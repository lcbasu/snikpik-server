package com.server.ud.dao.user_chat

import com.server.ud.entities.user_activity.UserChatsStatusByUserId
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface UserChatsStatusByUserIdRepository : CassandraRepository<UserChatsStatusByUserId?, String?> {
    fun findAllByUserIdAndChatId(userId: String, chatId: String): List<UserChatsStatusByUserId>
}
