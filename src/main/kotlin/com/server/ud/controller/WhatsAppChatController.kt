package com.server.ud.controller

import com.server.ud.dto.*
import com.server.ud.service.whatsapp_chat.WhatsAppChatService
import io.micrometer.core.annotation.Timed
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@Timed
@RequestMapping("ud/whatsAppChatBox")
class WhatsAppChatController {

    @Autowired
    private lateinit var whatsAppChatService: WhatsAppChatService

    @RequestMapping(value = ["/saveWhatsAppNumberDetails"], method = [RequestMethod.POST])
    fun saveWhatsAppNumberDetails(@RequestBody request: SaveWhatsAppNumberDetailRequest?): SavedWhatsAppNumberDetailResponse? {
        return whatsAppChatService.saveWhatsAppNumberDetails(request)
    }

    @RequestMapping(value = ["/getAllWhatsAppNumbersDetails"], method = [RequestMethod.GET])
    fun getAllWhatsAppNumbersDetails(): AllWhatsAppNumbersDetailsResponse? {
        return whatsAppChatService.getAllWhatsAppNumbersDetails()
    }

    @RequestMapping(value = ["/saveWhatsAppChatTracker"], method = [RequestMethod.POST])
    fun saveWhatsAppChatTracker(@RequestBody request: SaveWhatsAppChatTrackerRequest?): SavedWhatsAppChatTrackerResponse? {
        return whatsAppChatService.saveWhatsAppChatTracker(request)
    }
}
