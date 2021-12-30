package com.server.ud.dao.user_chat

import com.server.ud.entities.user_activity.UserChatMessage
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface UserChatMessageRepository : CassandraRepository<UserChatMessage?, String?> {}
