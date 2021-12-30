package com.server.ud.service.user_chat

import com.server.common.provider.SecurityProvider
import com.server.ud.dto.*
import com.server.ud.provider.user_chat.UserChatProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserChatServiceImpl: UserChatService() {

    @Autowired
    private lateinit var securityProvider: SecurityProvider

    @Autowired
    private lateinit var userChatProvider: UserChatProvider

    override fun getChatId(userId1: String, userId2: String): UserChatIdResponse {
        return UserChatIdResponse(
            chatId = userChatProvider.getChatId(userId1, userId2),
            userId1 = userId1,
            userId2 = userId2,
        )
    }

    override fun getUserChats(request: UserChatsFeedRequest): UserChatsFeedResponse {
        val result = userChatProvider.getUserChatsFeed(request)
        return UserChatsFeedResponse(
            chats = result.content?.filterNotNull()?.map { it.toUserChatsByUserIdResponse() } ?: emptyList(),
            count = result.count,
            hasNext = result.hasNext,
            pagingState = result.pagingState
        )
    }

    override fun saveChatMessage(request: SaveUserChatMessageRequest): SavedUserChatMessageResponse {
        validateChatId(request.chatId)
        val userChatIdResponse = getChatId(request.senderUserId, request.receiverUserId)
        if (userChatIdResponse.chatId != request.chatId) {
            error("Invalid chat Id for message. chatId: ${request.chatId} senderUserId: ${request.senderUserId}, receiverUserId: ${request.receiverUserId}")
        }

        val userDetailsFromToken = securityProvider.validateRequest()
        if (userDetailsFromToken.getUserIdToUse() != request.senderUserId) {
            error("Only logged in user can send message on their behalf. loggedInUserId: ${userDetailsFromToken.getUserIdToUse()}, messageSenderId: ${request.senderUserId}")
        }
        return userChatProvider.saveChatMessage(request).toSavedUserChatMessageResponse()
    }

    override fun getUserChatMessages(request: UserChatMessagesFeedRequest): UserChatMessagesFeedResponse {
        validateChatId(request.chatId)
        val result = userChatProvider.getUserChatMessagesFeed(request)
        return UserChatMessagesFeedResponse(
            messages = result.content?.filterNotNull()?.map { it.toSavedUserChatMessageResponse() } ?: emptyList(),
            count = result.count,
            hasNext = result.hasNext,
            pagingState = result.pagingState
        )
    }

    override fun getChatMessagesCount(chatId: String): UserChatMessagesCountResponse? {
        validateChatId(chatId)
        return userChatProvider.getChatMessagesCount(chatId)?.toUserChatMessagesCountResponse()
    }

    override fun getChatsLastMessage(chatId: String): UserChatsLastMessageResponse? {
        validateChatId(chatId)
        return userChatProvider.getChatsLastMessage(chatId)?.toUserChatsLastMessageResponse()
    }

    override fun getChatMessageStatusForUser(request: UserChatMessageStatusForUserRequest): UserChatMessageStatusForUserResponse? {
        validateChatId(request.chatId)
        return userChatProvider.getChatMessageStatusForUser(request)?.toUserChatMessageStatusForUserResponse()
    }

    override fun getChatStatusForUser(request: UserChatStatusForUserRequest): UserChatStatusForUserResponse? {
        validateChatId(request.chatId)
        return userChatProvider.getChatStatusForUser(request)?.toUserChatStatusForUserResponse()
    }

    private fun validateChatId(chatId: String) {
        val userDetailsFromToken = securityProvider.validateRequest()
        val userChatIdResponse = userChatProvider.getUserIdsFromChatId(chatId)
        if (userChatIdResponse.userId1 != userDetailsFromToken.getUserIdToUse() && userChatIdResponse.userId2 != userDetailsFromToken.getUserIdToUse()) {
            error("This chat does not belong to the logged in user. loggedInUserId: ${userDetailsFromToken.getUserIdToUse()}, chatUserId1: ${userChatIdResponse.userId1}, chatUser2: ${userChatIdResponse.userId2}")
        }
    }

}
