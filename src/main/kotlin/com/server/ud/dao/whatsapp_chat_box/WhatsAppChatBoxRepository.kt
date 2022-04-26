package com.server.ud.dao.whatsapp_chat_box

import com.server.ud.entities.whatsapp_chat.WhatsAppChatTrackerByUser
import com.server.ud.entities.whatsapp_chat.WhatsAppNumbersByUser
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface WhatsAppNumbersByUserRepository : CassandraRepository<WhatsAppNumbersByUser?, String?> {
}

@Repository
interface WhatsAppChatTrackerByUserRepository : CassandraRepository<WhatsAppChatTrackerByUser?, String?> {
}
