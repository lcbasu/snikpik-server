package com.server.ud.entities.user

import com.server.common.enums.NotificationTokenProvider
import com.server.ud.enums.UserProfession
import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.time.Instant
import javax.persistence.EnumType
import javax.persistence.Enumerated

@Table("users")
class UserV2 (

    @PrimaryKeyColumn(name = "user_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var userId: String,

    @PrimaryKeyColumn(name = "created_at", ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    var createdAt: Instant = Instant.now(),

    @Column("absolute_mobile")
    var absoluteMobile: String? = null,

    @Column("country_code")
    var countryCode: String? = null,

    @Column("handle")
    var handle: String? = null,

    @Column
    var uid: String? = "",

    @Column
    var anonymous: Boolean = false,

    @Column
    var verified: Boolean = false,

    @Column
    var profession: UserProfession? = null,

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

