package com.server.ud.dao.user_chat

import com.server.ud.entities.user_activity.UserChatsByUserId
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Repository

@Repository
interface UserChatsByUserIdRepository : CassandraRepository<UserChatsByUserId?, String?> {
    fun findAllByUserId(userId: String, pageable: Pageable): Slice<UserChatsByUserId>
    fun findAllByUserIdAndChatId(userId: String, chatId: String): List<UserChatsByUserId>
}

