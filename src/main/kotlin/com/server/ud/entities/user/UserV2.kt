package com.server.ud.entities.user

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.server.common.enums.NotificationTokenProvider
import com.server.common.enums.ProfileType
import com.server.common.utils.DateUtils
import com.server.dk.model.MediaDetailsV2
import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.time.Instant
import javax.persistence.EnumType
import javax.persistence.Enumerated

@Table("users")
data class UserV2 (

    @PrimaryKeyColumn(name = "user_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var userId: String,

    @PrimaryKeyColumn(name = "created_at", ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    var createdAt: Instant = DateUtils.getInstantNow(),

    @Column("absolute_mobile")
    var absoluteMobile: String? = null,

    @Column("country_code")
    var countryCode: String? = null,

    @Column("handle")
    var handle: String? = null,

    @Column
    var dp: String? = null, // MediaDetailsV2

    @Column
    var uid: String? = "",

    @Column
    var anonymous: Boolean = false,

    @Column
    var verified: Boolean = false,

    @Column
    var profiles: String? = null, // Set of ProfileType as String

    @Column("full_name")
    var fullName: String? = "",

    @Column("notification_token")
    var notificationToken: String? = "",

    @Column("notification_token_provider")
    @Enumerated(EnumType.STRING)
    var notificationTokenProvider: NotificationTokenProvider? = NotificationTokenProvider.FIREBASE,

    @Column("user_last_zipcode")
    var userLastLocationZipcode: String? = null,

    @Column("user_last_google_place_id")
    var userLastGooglePlaceId: String? = null,

    @Column("user_last_location_id")
    var userLastLocationId: String? = null,

    @Column("user_last_location_name")
    val userLastLocationName: String? = null,

    @Column("user_last_location_lat")
    val userLastLocationLat: Double? = null,

    @Column("user_last_location_lng")
    val userLastLocationLng: Double? = null,
)

fun UserV2.getMediaDetailsForDP(): MediaDetailsV2? {
    this.apply {
        return try {
            jacksonObjectMapper().readValue(dp, MediaDetailsV2::class.java)
        } catch (e: Exception) {
            null
        }
    }
}

fun UserV2.getProfiles(): Set<ProfileType> {
    this.apply {
        return try {
            val profileIds = profiles?.trim()?.split(",") ?: emptySet()
            return profileIds.map {
                ProfileType.valueOf(it)
            }.toSet()
        } catch (e: Exception) {
            e.printStackTrace()
            emptySet()
        }
    }
}
