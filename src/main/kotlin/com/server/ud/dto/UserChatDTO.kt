package com.server.ud.dto

import com.server.common.dto.AllCategoryV2Response
import com.server.common.model.MediaDetailsV2
import com.server.common.utils.DateUtils
import com.server.ud.entities.user_activity.*
import com.server.ud.enums.CategoryV2
import com.server.ud.enums.UserChatMessageStatus
import com.server.ud.enums.UserChatStatus

data class UserChatIdResponse (
    val chatId: String,
    val userId1: String,
    val userId2: String,
)

data class SaveUserChatMessageRequest (
    val chatId: String,
//    val senderUserId: String,
//    val receiverUserId: String,
    val text: String? = null,
    val media: MediaDetailsV2? = null, // MediaDetailsV2
    val categories: Set<CategoryV2>? = null, //  Set of CategoryV2
    val locationId: String? = null,
    val googlePlaceId: String? = null,
    val zipcode: String? = null,
    val locationName: String? = null,
    val locationLat: Double? = null,
    val locationLng: Double? = null,
    val locality: String? = null,
    val subLocality: String? = null,
    val route: String? = null,
    val city: String? = null,
    val state: String? = null,
    val country: String? = null,
    val countryCode: String? = null,
    val completeAddress: String? = null,
)

data class SavedUserChatMessageResponse (
    val messageId: String,
    val createdAt: Long,
    val chatId: String,
    val senderUserId: String,
    val receiverUserId: String,
    val text: String? = null,
    val media: MediaDetailsV2? = null, // MediaDetailsV2
    val categories: AllCategoryV2Response? = null, //  AllCategoryV2Response
    val locationId: String? = null,
    val googlePlaceId: String? = null,
    val zipcode: String? = null,
    val locationName: String? = null,
    val locationLat: Double? = null,
    val locationLng: Double? = null,
    val locality: String? = null,
    val subLocality: String? = null,
    val route: String? = null,
    val city: String? = null,
    val state: String? = null,
    val country: String? = null,
    val countryCode: String? = null,
    val completeAddress: String? = null,
)

data class UserChatMessagesFeedResponse(
    val messages: List<SavedUserChatMessageResponse>,
    override val count: Int? = null,
    override val pagingState: String? = null,
    override val hasNext: Boolean? = null,
): PaginationResponse(count, pagingState, hasNext)


data class UserChatMessagesFeedRequest (
    val chatId: String,
    override val limit: Int = 10,
    override val pagingState: String? = null,
): PaginationRequest(limit, pagingState)

data class UserChatMessagesCountResponse (
    val chatId: String,
    val messagesCount: Long = 0
)

data class UserChatsByUserIdResponse (
    val userId: String,
    val chatId: String,
    val updatedAt: Long,
)

data class UserChatsFeedResponse(
    val chats: List<UserChatsByUserIdResponse>,
    override val count: Int? = null,
    override val pagingState: String? = null,
    override val hasNext: Boolean? = null,
): PaginationResponse(count, pagingState, hasNext)

data class UserChatsFeedRequest (
    val userId: String,
    override val limit: Int = 10,
    override val pagingState: String? = null,
): PaginationRequest(limit, pagingState)

data class UserChatsLastMessageResponse (
    val chatId: String,
    val messageId: String,
    val createdAt: Long,
    val senderUserId: String,
    val receiverUserId: String,
    val text: String? = null,
    val media: MediaDetailsV2? = null, // MediaDetailsV2
    val categories: AllCategoryV2Response? = null, //  AllCategoryV2Response
    val locationId: String? = null,
    val googlePlaceId: String? = null,
    val zipcode: String? = null,
    val locationName: String? = null,
    val locationLat: Double? = null,
    val locationLng: Double? = null,
    val locality: String? = null,
    val subLocality: String? = null,
    val route: String? = null,
    val city: String? = null,
    val state: String? = null,
    val country: String? = null,
    val countryCode: String? = null,
    val completeAddress: String? = null,
)

data class UserChatMessageStatusForUserRequest (
    val userId: String,
    val chatId: String,
    val messageId: String,
)

data class UserChatMessageStatusForUserResponse (
    val userId: String,
    val chatId: String,
    val messageId: String,
    val chatMessageStatus: UserChatMessageStatus,
)

data class UserChatStatusForUserRequest (
    val userId: String,
    val chatId: String,
)

data class UserChatStatusForUserResponse (
    val userId: String,
    val chatId: String,
    val chatStatus: UserChatStatus,
)

fun UserChatsByUserId.toUserChatsByUserIdResponse(): UserChatsByUserIdResponse {
    this.apply {
        return UserChatsByUserIdResponse(
            userId = userId,
            chatId = chatId,
            updatedAt = DateUtils.getEpoch(updatedAt),
        )
    }
}

fun UserChatMessage.toSavedUserChatMessageResponse(): SavedUserChatMessageResponse {
    this.apply {
        return SavedUserChatMessageResponse(
            messageId = messageId,
            createdAt = DateUtils.getEpoch(createdAt),
            chatId = chatId,
            senderUserId = senderUserId,
            receiverUserId = receiverUserId,
            text = text,
            media = getMediaDetails(),
            categories = getCategories(),
            locationId = locationId,
            googlePlaceId = googlePlaceId,
            zipcode = zipcode,
            locationName = locationName,
            locationLat = locationLat,
            locationLng = locationLng,
            locality = locality,
            subLocality = subLocality,
            route = route,
            city = city,
            state = state,
            country = country,
            countryCode = countryCode,
            completeAddress = completeAddress,
        )
    }
}

fun UserChatMessageByChatId.toSavedUserChatMessageResponse(): SavedUserChatMessageResponse {
    this.apply {
        return SavedUserChatMessageResponse(
            messageId = messageId,
            createdAt = DateUtils.getEpoch(createdAt),
            chatId = chatId,
            senderUserId = senderUserId,
            receiverUserId = receiverUserId,
            text = text,
            media = getMediaDetails(),
            categories = getCategories(),
            locationId = locationId,
            googlePlaceId = googlePlaceId,
            zipcode = zipcode,
            locationName = locationName,
            locationLat = locationLat,
            locationLng = locationLng,
            locality = locality,
            subLocality = subLocality,
            route = route,
            city = city,
            state = state,
            country = country,
            countryCode = countryCode,
            completeAddress = completeAddress,
        )
    }
}


fun UserChatsLastMessage.toUserChatsLastMessageResponse(): UserChatsLastMessageResponse {
    this.apply {
        return UserChatsLastMessageResponse(
            messageId = messageId,
            createdAt = DateUtils.getEpoch(createdAt),
            chatId = chatId,
            senderUserId = senderUserId,
            receiverUserId = receiverUserId,
            text = text,
            media = getMediaDetails(),
            categories = getCategories(),
            locationId = locationId,
            googlePlaceId = googlePlaceId,
            zipcode = zipcode,
            locationName = locationName,
            locationLat = locationLat,
            locationLng = locationLng,
            locality = locality,
            subLocality = subLocality,
            route = route,
            city = city,
            state = state,
            country = country,
            countryCode = countryCode,
            completeAddress = completeAddress,
        )
    }
}

fun UserChatMessagesCount.toUserChatMessagesCountResponse(): UserChatMessagesCountResponse {
    this.apply {
        return UserChatMessagesCountResponse(
            chatId = chatId!!,
            messagesCount = messagesCount ?: 0
        )
    }
}


fun UserChatsMessageStatusByUserId.toUserChatMessageStatusForUserResponse(): UserChatMessageStatusForUserResponse {
    this.apply {
        return UserChatMessageStatusForUserResponse(
            userId = userId,
            chatId = chatId,
            messageId = messageId,
            chatMessageStatus = chatMessageStatus,
        )
    }
}

fun UserChatsStatusByUserId.toUserChatStatusForUserResponse(): UserChatStatusForUserResponse {
    this.apply {
        return UserChatStatusForUserResponse(
            userId = userId,
            chatId = chatId,
            chatStatus = chatStatus,
        )
    }
}
