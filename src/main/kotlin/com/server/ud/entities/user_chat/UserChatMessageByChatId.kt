package com.server.ud.entities.user_activity

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.server.common.model.MediaDetailsV2
import com.server.common.model.getMediaDetailsFromJsonString
import com.server.common.utils.DateUtils
import com.server.common.dto.AllCategoryV2Response
import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.time.Instant

@Table("user_chat_messages_by_chat")
class UserChatMessageByChatId (

    @PrimaryKeyColumn(name = "chat_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var chatId: String,

    @PrimaryKeyColumn(name = "created_at", ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    var createdAt: Instant = DateUtils.getInstantNow(),

    @Column("message_id")
    var messageId: String,

    @Column("sender_user_id")
    var senderUserId: String,

    @Column("receiver_user_id")
    var receiverUserId: String,

    @Column
    val text: String? = null,

    @Column
    var media: String? = null, // MediaDetailsV2

    @Column
    var categories: String? = null, //  List of AllCategoryV2Response

    @Column("location_id")
    var locationId: String? = null,

    @Column("google_place_id")
    var googlePlaceId: String? = null,

    @Column("zipcode")
    var zipcode: String? = null,

    @Column("location_name")
    val locationName: String? = null,

    @Column("location_lat")
    val locationLat: Double? = null,

    @Column("location_lng")
    val locationLng: Double? = null,

    @Column
    val locality: String? = null,

    @Column("sub_locality")
    val subLocality: String? = null,

    @Column
    val route: String? = null,

    @Column
    val city: String? = null,

    @Column
    val state: String? = null,

    @Column
    val country: String? = null,

    @Column("country_code")
    val countryCode: String? = null,

    @Column("complete_address")
    val completeAddress: String? = null,
)

fun UserChatMessageByChatId.getCategories(): AllCategoryV2Response {
    this.apply {
        return try {
            jacksonObjectMapper().readValue(categories, AllCategoryV2Response::class.java)
        } catch (e: Exception) {
            AllCategoryV2Response(emptyList())
        }
    }
}

fun UserChatMessageByChatId.getMediaDetails(): MediaDetailsV2 {
    this.apply {
        return getMediaDetailsFromJsonString(media)
    }
}
