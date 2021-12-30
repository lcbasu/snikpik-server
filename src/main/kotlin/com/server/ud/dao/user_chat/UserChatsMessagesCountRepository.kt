package com.server.ud.dao.user_chat

import com.server.ud.entities.user_activity.UserChatMessagesCount
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.cassandra.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface UserChatsMessagesCountRepository : CassandraRepository<UserChatMessagesCount?, String?> {
    fun findAllByChatId(chatId: String): List<UserChatMessagesCount>

    @Query("UPDATE user_chat_messages_count SET messages_count = messages_count + 1 WHERE chat_id = ?0")
    fun incrementMessageCount(chatId: String)

//    @Query("UPDATE user_chat_messages_count SET messages_count = messages_count - 1 WHERE chat_id = ?0")
//    fun decrementMessageCount(chatId: String)
}
