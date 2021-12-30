package com.server.ud.dao.user_chat

import com.server.ud.entities.user_activity.UserChatMessageByChatId
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Repository

@Repository
interface UserChatMessageByChatIdRepository : CassandraRepository<UserChatMessageByChatId?, String?> {
    fun findAllByChatId(chatId: String, pageable: Pageable): Slice<UserChatMessageByChatId>
}
