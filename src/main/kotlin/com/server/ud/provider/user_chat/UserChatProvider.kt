package com.server.ud.provider.user_chat

import com.server.common.enums.ReadableIdPrefix
import com.server.common.model.convertToString
import com.server.common.provider.RandomIdProvider
import com.server.common.utils.CommonUtils
import com.server.common.utils.DateUtils
import com.server.ud.dao.user_chat.*
import com.server.ud.dto.*
import com.server.ud.entities.user_activity.*
import com.server.ud.enums.UserChatMessageStatus
import com.server.ud.enums.UserChatStatus
import com.server.ud.pagination.CassandraPageV2
import com.server.ud.utils.pagination.PaginationRequestUtil
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class UserChatProvider {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var userChatsByUserIdRepository: UserChatsByUserIdRepository

    @Autowired
    private lateinit var userChatMessageRepository: UserChatMessageRepository

    @Autowired
    private lateinit var userChatMessageByChatIdRepository: UserChatMessageByChatIdRepository

    @Autowired
    private lateinit var userChatsMessagesCountRepository: UserChatsMessagesCountRepository

    @Autowired
    private lateinit var userChatsLastMessageRepository: UserChatsLastMessageRepository

    @Autowired
    private lateinit var userChatsMessageStatusByUserIdRepository: UserChatsMessageStatusByUserIdRepository

    @Autowired
    private lateinit var userChatsStatusByUserIdRepository: UserChatsStatusByUserIdRepository

    @Autowired
    private lateinit var paginationRequestUtil: PaginationRequestUtil

    @Autowired
    private lateinit var randomIdProvider: RandomIdProvider

    fun getChatId(userId1: String, userId2: String): String {
        val users = listOf(userId1, userId2)
        return users.sorted().joinToString(CommonUtils.STRING_SEPARATOR)
    }

    fun getUserIdsFromChatId(chatId: String): UserChatIdResponse {
        chatId.split(CommonUtils.STRING_SEPARATOR).let {
            return UserChatIdResponse(chatId, it[0], it[1])
        }
    }

    fun getUserChatsFeed(request: UserChatsFeedRequest): CassandraPageV2<UserChatsByUserId> {
        return getPaginatedFeedForUserChats(
            userId = request.userId,
            limit = request.limit,
            pagingState = request.pagingState,
        )
    }

    private fun getPaginatedFeedForUserChats(userId: String, limit: Int, pagingState: String? = null): CassandraPageV2<UserChatsByUserId> {
        val pageRequest = paginationRequestUtil.createCassandraPageRequest(limit, pagingState)
        val chats = userChatsByUserIdRepository.findAllByUserId(userId, pageRequest as Pageable)
        return CassandraPageV2(chats)
    }

    fun getUserChatMessagesFeed(request: UserChatMessagesFeedRequest): CassandraPageV2<UserChatMessageByChatId> {
        return getPaginatedFeedForChatMessages(
            chatId = request.chatId,
            limit = request.limit,
            pagingState = request.pagingState,
        )
    }

    private fun getPaginatedFeedForChatMessages(chatId: String, limit: Int, pagingState: String? = null): CassandraPageV2<UserChatMessageByChatId> {
        val pageRequest = paginationRequestUtil.createCassandraPageRequest(limit, pagingState)
        val messages = userChatMessageByChatIdRepository.findAllByChatId(chatId, pageRequest as Pageable)
        return CassandraPageV2(messages)
    }

    fun saveChatMessage(loggedInUserId: String, request: SaveUserChatMessageRequest): UserChatMessage {
        val userChatIdResponse = getUserIdsFromChatId(request.chatId)
        val receiverUserId = if (userChatIdResponse.userId1 == loggedInUserId) {
            userChatIdResponse.userId2
        } else {
            userChatIdResponse.userId1
        }
        val chatMessage = userChatMessageRepository.save(UserChatMessage(
            messageId = randomIdProvider.getRandomIdFor(ReadableIdPrefix.UCT),
            createdAt = DateUtils.getInstantNow(),
            chatId = request.chatId,
            senderUserId = loggedInUserId,
            receiverUserId = receiverUserId,
            text = request.text,
            media = request.media?.convertToString(),
            categories = AllCategoryV2Response(
                categories = request.categories?.map { it.toCategoryV2Response() } ?: emptyList()
            ).convertToString(),
            locationId = request.locationId,
            googlePlaceId = request.googlePlaceId,
            zipcode = request.zipcode,
            locationName = request.locationName,
            locationLat = request.locationLat,
            locationLng = request.locationLng,
            locality = request.locality,
            subLocality = request.subLocality,
            route = request.route,
            city = request.city,
            state = request.state,
            country = request.country,
            countryCode = request.countryCode,
            completeAddress = request.completeAddress,
        ))

        userChatMessageByChatIdRepository.save(
            UserChatMessageByChatId(
                messageId = chatMessage.messageId,
                createdAt = chatMessage.createdAt,
                chatId = chatMessage.chatId,
                senderUserId = chatMessage.senderUserId,
                receiverUserId = chatMessage.receiverUserId,
                text = chatMessage.text,
                media = chatMessage.media,
                categories = chatMessage.categories,
                locationId = chatMessage.locationId,
                googlePlaceId = chatMessage.googlePlaceId,
                zipcode = chatMessage.zipcode,
                locationName = chatMessage.locationName,
                locationLat = chatMessage.locationLat,
                locationLng = chatMessage.locationLng,
                locality = chatMessage.locality,
                subLocality = chatMessage.subLocality,
                route = chatMessage.route,
                city = chatMessage.city,
                state = chatMessage.state,
                country = chatMessage.country,
                countryCode = chatMessage.countryCode,
                completeAddress = chatMessage.completeAddress,
            )
        )

        // Process chat message for user in background
        // Move this to better place like deferred processing
        GlobalScope.launch {

            // Delete the older chat row, to store the last as we want it to be sorted by updatedAt
            val chatsBySender = userChatsByUserIdRepository.findAllByUserIdAndChatId(chatMessage.senderUserId, chatMessage.chatId)
            userChatsByUserIdRepository.deleteAll(chatsBySender)
            val chatsByReceiver = userChatsByUserIdRepository.findAllByUserIdAndChatId(chatMessage.receiverUserId, chatMessage.chatId)
            userChatsByUserIdRepository.deleteAll(chatsByReceiver)
            userChatsByUserIdRepository.saveAll(listOf(
                UserChatsByUserId(
                    userId = chatMessage.senderUserId,
                    chatId = chatMessage.chatId,
                    updatedAt = chatMessage.createdAt,
                ),
                UserChatsByUserId(
                    userId = chatMessage.receiverUserId,
                    chatId = chatMessage.chatId,
                    updatedAt = chatMessage.createdAt,
                )
            ))



            userChatsMessagesCountRepository.incrementMessageCount(chatId = chatMessage.chatId)
            userChatsLastMessageRepository.save(
                UserChatsLastMessage(
                    messageId = chatMessage.messageId,
                    createdAt = chatMessage.createdAt,
                    chatId = chatMessage.chatId,
                    senderUserId = chatMessage.senderUserId,
                    receiverUserId = chatMessage.receiverUserId,
                    text = chatMessage.text,
                    media = chatMessage.media,
                    categories = chatMessage.categories,
                    locationId = chatMessage.locationId,
                    googlePlaceId = chatMessage.googlePlaceId,
                    zipcode = chatMessage.zipcode,
                    locationName = chatMessage.locationName,
                    locationLat = chatMessage.locationLat,
                    locationLng = chatMessage.locationLng,
                    locality = chatMessage.locality,
                    subLocality = chatMessage.subLocality,
                    route = chatMessage.route,
                    city = chatMessage.city,
                    state = chatMessage.state,
                    country = chatMessage.country,
                    countryCode = chatMessage.countryCode,
                    completeAddress = chatMessage.completeAddress,
                )
            )

            userChatsMessageStatusByUserIdRepository.save(
                UserChatsMessageStatusByUserId(
                    userId = chatMessage.senderUserId,
                    chatId = chatMessage.chatId,
                    messageId = chatMessage.messageId,
                    chatMessageStatus = UserChatMessageStatus.READ,
                )
            )
            userChatsMessageStatusByUserIdRepository.save(
                UserChatsMessageStatusByUserId(
                    userId = chatMessage.receiverUserId,
                    chatId = chatMessage.chatId,
                    messageId = chatMessage.messageId,
                    chatMessageStatus = UserChatMessageStatus.DELIVERED,
                )
            )

            userChatsStatusByUserIdRepository.save(
                UserChatsStatusByUserId(
                    userId = chatMessage.senderUserId,
                    chatId = chatMessage.chatId,
                    chatStatus = UserChatStatus.READ,
                )
            )
            userChatsStatusByUserIdRepository.save(
                UserChatsStatusByUserId(
                    userId = chatMessage.receiverUserId,
                    chatId = chatMessage.chatId,
                    chatStatus = UserChatStatus.DELIVERED,
                )
            )
        }
        return chatMessage
    }

    fun getChatMessagesCount(chatId: String): UserChatMessagesCount? {
        return try {
            val chats = userChatsMessagesCountRepository.findAllByChatId(chatId)
            if (chats.size > 1) {
                error("More than one chats count has same chatId: $chatId")
            }
            chats.firstOrNull()
        } catch (e: Exception) {
            logger.error("Getting chats count for $chatId failed.")
            e.printStackTrace()
            null
        }
    }

    fun getChatsLastMessage(chatId: String): UserChatsLastMessage? {
        return try {
            val chats = userChatsLastMessageRepository.findAllByChatId(chatId)
            if (chats.size > 1) {
                error("More than one chat has same chatId: $chatId")
            }
            chats.firstOrNull()
        } catch (e: Exception) {
            logger.error("Getting Last Message for chat for $chatId failed.")
            e.printStackTrace()
            null
        }
    }

    fun getChatMessageStatusForUser(request: UserChatMessageStatusForUserRequest): UserChatsMessageStatusByUserId? {
        return try {
            val chats = userChatsMessageStatusByUserIdRepository.findAllByUserIdAndChatIdAndMessageId(
                request.userId,
                request.chatId,
                request.messageId
            )
            if (chats.size > 1) {
                error("More than one chat message status has same chatId: ${request.chatId}")
            }
            chats.firstOrNull()
        } catch (e: Exception) {
            logger.error("Getting chat message status for chat for ${request.chatId} failed.")
            e.printStackTrace()
            null
        }
    }

    fun getChatStatusForUser(request: UserChatStatusForUserRequest): UserChatsStatusByUserId? {
        return try {
            val chats = userChatsStatusByUserIdRepository.findAllByUserIdAndChatId(
                request.userId,
                request.chatId,
            )
            if (chats.size > 1) {
                error("More than one chat status has same chatId: ${request.chatId}")
            }
            chats.firstOrNull()
        } catch (e: Exception) {
            logger.error("Getting chat status for chat for ${request.chatId} failed.")
            e.printStackTrace()
            null
        }
    }

}
