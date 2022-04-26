package com.server.ud.entities.whatsapp_chat

import com.server.common.utils.DateUtils
import com.server.ud.enums.WhatsAppChatTrackerType
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.time.Instant

@Table("whatsapp_numbers_by_user")
data class WhatsAppNumbersByUser(

    @PrimaryKeyColumn(name = "user_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var userId: String,

    @PrimaryKeyColumn(name = "absolute_whatsapp_number", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    var absoluteWhatsappNumber: String,

    @Column("created_at")
    var createdAt: Instant = DateUtils.getInstantNow(),

    @Column
    val icon: String? = null, // MediaDetailsV2

    @Column
    var title: String? = null,

    @Column
    var description: String? = null,

    @Column("greeting_message")
    var greetingMessage: String? = null,

)

@Table("whatsapp_chat_tracker_by_user")
data class WhatsAppChatTrackerByUser(

    @PrimaryKeyColumn(name = "user_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var userId: String,

    // For chat level tracking, the actual whatsapp number will be null hence keeping COMMON_NO_NUMBER as default value
    @PrimaryKeyColumn(name = "absolute_whatsapp_number", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    var absoluteWhatsappNumber: String = "COMMON_NO_NUMBER",

    @PrimaryKeyColumn(name = "event_type", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
    var eventType: WhatsAppChatTrackerType,

    @PrimaryKeyColumn(name = "created_at", ordinal = 3, type = PrimaryKeyType.CLUSTERED)
    var createdAt: Instant = DateUtils.getInstantNow(),

    // To make the entries unique so that each interaction is unique even if the timestamp is same
    @PrimaryKeyColumn(name = "request_id", ordinal = 4, type = PrimaryKeyType.CLUSTERED)
    var requestId: String,

    @Column("ip_address")
    var ipAddress: String? = null,

    @Column("ip_data")
    var ipData: String? = null,

    )

