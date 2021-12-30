package com.server.ud.service.user_chat

import com.server.ud.dto.*

abstract class UserChatService {
    abstract fun getChatId(userId1: String, userId2: String): UserChatIdResponse
    abstract fun getUserChats(request: UserChatsFeedRequest): UserChatsFeedResponse
    abstract fun saveChatMessage(request: SaveUserChatMessageRequest): SavedUserChatMessageResponse
    abstract fun getUserChatMessages(request: UserChatMessagesFeedRequest): UserChatMessagesFeedResponse
    abstract fun getChatMessagesCount(chatId: String): UserChatMessagesCountResponse?
    abstract fun getChatsLastMessage(chatId: String): UserChatsLastMessageResponse?
    abstract fun getChatMessageStatusForUser(request: UserChatMessageStatusForUserRequest): UserChatMessageStatusForUserResponse?
    abstract fun getChatStatusForUser(request: UserChatStatusForUserRequest): UserChatStatusForUserResponse?
}
