package com.server.ud.service.whatsapp_chat

import com.server.ud.dto.*

abstract class WhatsAppChatService {
    abstract fun saveWhatsAppNumberDetails(request: SaveWhatsAppNumberDetailRequest?): SavedWhatsAppNumberDetailResponse?
    abstract fun getAllWhatsAppNumbersDetails(): AllWhatsAppNumbersDetailsResponse?
    abstract fun saveWhatsAppChatTracker(request: SaveWhatsAppChatTrackerRequest?): SavedWhatsAppChatTrackerResponse?

}
