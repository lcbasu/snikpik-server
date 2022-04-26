package com.server.ud.dto

import com.server.common.dto.SavedUserV2Response
import com.server.common.model.MediaDetailsV2
import com.server.ud.enums.WhatsAppChatTrackerType

data class SaveWhatsAppNumberDetailRequest(
    var absoluteWhatsappNumber: String,
    val icon: MediaDetailsV2?,
    var title: String?,
    var description: String?,
    var greetingMessage: String?,
)

data class SavedWhatsAppNumberDetailResponse(
    var absoluteWhatsappNumber: String,
    var createdAt: Long,
    val icon: MediaDetailsV2?,
    var title: String?,
    var description: String?,
    var greetingMessage: String?,

)

data class AllWhatsAppNumbersDetailsResponse(
    var user: SavedUserV2Response,
    val whatsAppNumbersDetails: List<SavedWhatsAppNumberDetailResponse>,
)

data class SaveWhatsAppChatTrackerRequest(
    var userId: String,
    var absoluteWhatsappNumber: String = "COMMON_NO_NUMBER",
    var eventType: WhatsAppChatTrackerType,
    var requestId: String,
    var ipAddress: String? = null,
    var ipData: String? = null,
)

data class SavedWhatsAppChatTrackerResponse(
    var userId: String,
    var absoluteWhatsappNumber: String,
    var eventType: WhatsAppChatTrackerType,
    var requestId: String,
    var ipAddress: String? = null,
    var ipData: String? = null,
)
