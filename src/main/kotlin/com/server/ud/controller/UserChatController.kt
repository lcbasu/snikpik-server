package com.server.ud.controller

import com.server.common.provider.SecurityProvider
import com.server.ud.dto.*
import com.server.ud.service.user_chat.UserChatService
import io.micrometer.core.annotation.Timed
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@Timed
@RequestMapping("ud/userChat")
class UserChatController {

    @Autowired
    private lateinit var securityProvider: SecurityProvider

    @Autowired
    private lateinit var userChatService: UserChatService

    @RequestMapping(value = ["/saveChatMessage"], method = [RequestMethod.POST])
    fun saveChatMessage(@RequestBody request: SaveUserChatMessageRequest): SavedUserChatMessageResponse {
        return userChatService.saveChatMessage(request)
    }

    @RequestMapping(value = ["/getChatMessagesCount"], method = [RequestMethod.GET])
    fun getChatMessagesCount(@RequestParam chatId: String): UserChatMessagesCountResponse? {
        return userChatService.getChatMessagesCount(chatId)
    }


    @RequestMapping(value = ["/getChatMessageStatusForUser"], method = [RequestMethod.GET])
    fun getChatMessageStatusForUser(@RequestParam userId: String,
                                     @RequestParam chatId: String,
                                     @RequestParam messageId: String): UserChatMessageStatusForUserResponse? {
        return userChatService.getChatMessageStatusForUser(UserChatMessageStatusForUserRequest (
            userId = userId,
            chatId = chatId,
            messageId = messageId,
        ))
    }

    @RequestMapping(value = ["/getChatStatusForUser"], method = [RequestMethod.GET])
    fun getChatStatusForUser(@RequestParam userId: String,
                             @RequestParam chatId: String): UserChatStatusForUserResponse? {
        return userChatService.getChatStatusForUser(UserChatStatusForUserRequest (
            userId = userId,
            chatId = chatId,
        ))
    }

    @RequestMapping(value = ["/getChatsLastMessage"], method = [RequestMethod.GET])
    fun getChatsLastMessage(@RequestParam chatId: String): UserChatsLastMessageResponse? {
        return userChatService.getChatsLastMessage(chatId)
    }

    @RequestMapping(value = ["/getChatId"], method = [RequestMethod.GET])
    fun getChatId(@RequestParam withUserId: String): UserChatIdResponse {
        val userDetailsFromToken = securityProvider.validateRequest()
        return userChatService.getChatId(userDetailsFromToken.getUserIdToUse(), withUserId)
    }

    @RequestMapping(value = ["/getUserChats"], method = [RequestMethod.GET])
    fun getUserChats(@RequestParam limit: Int,
                     @RequestParam pagingState: String? = null): UserChatsFeedResponse {
        val userDetailsFromToken = securityProvider.validateRequest()
        return userChatService.getUserChats(
            UserChatsFeedRequest(
                userDetailsFromToken.getUserIdToUse(),
                limit,
                pagingState
            )
        )
    }

    @RequestMapping(value = ["/getUserChatMessages"], method = [RequestMethod.GET])
    fun getUserChatMessages(@RequestParam chatId: String,
                            @RequestParam limit: Int,
                            @RequestParam pagingState: String? = null): UserChatMessagesFeedResponse {
        return userChatService.getUserChatMessages(
            UserChatMessagesFeedRequest(
                chatId,
                limit,
                pagingState
            )
        )
    }

}
