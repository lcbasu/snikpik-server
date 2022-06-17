package com.server.sp.entities.user

import com.server.common.enums.NotificationTokenProvider
import com.server.common.model.MediaDetailsV2
import com.server.common.model.getMediaDetailsFromJsonString
import com.server.common.utils.DateUtils
import com.server.sp.dto.SavedSpUserResponse
import com.server.sp.dto.SpUserPublicMiniDataResponse
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.CassandraType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.time.Instant
import javax.persistence.EnumType
import javax.persistence.Enumerated

@Table("sp_users")
data class SpUser (

    @PrimaryKeyColumn(name = "user_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    val userId: String,

    @Column("created_at")
    @CassandraType(type = CassandraType.Name.TIMESTAMP)
    var createdAt: Instant = DateUtils.getInstantNow(),

    @Column("absolute_mobile")
    val absoluteMobile: String? = null,

    @Column("country_code")
    val countryCode: String? = null,

    @Column("handle")
    val handle: String? = null,

    @Column
    val email: String? = null,

    @Column
    val dp: String? = null, // MediaDetailsV2

    @Column("cover_image")
    val coverImage: String? = null, // MediaDetailsV2

    @Column
    val uid: String? = "",

    @Column
    val anonymous: Boolean = false,

    // This is false if the user account was created by inviting the user by friend.
    // This is true if the user account was created by user themselves logging in
    @Column("at_least_once_logged_in")
    val atLeastOnceLoggedIn: Boolean = true,

    @Column
    val verified: Boolean = false,

    @Column("contact_visible")
    val contactVisible: Boolean? = null,

    @Column("full_name")
    val fullName: String? = "",

    @Column("notification_token")
    val notificationToken: String? = "",

    @Column("notification_token_provider")
    @Enumerated(EnumType.STRING)
    val notificationTokenProvider: NotificationTokenProvider? = NotificationTokenProvider.FIREBASE,
)

fun SpUser.getMediaDetailsForDP(): MediaDetailsV2 {
    this.apply {
        return getMediaDetailsFromJsonString(dp)
    }
}

fun SpUser.getMediaDetailsForCoverImage(): MediaDetailsV2 {
    this.apply {
        return getMediaDetailsFromJsonString(coverImage)
    }
}

fun SpUser.toSavedSpUserResponse(): SavedSpUserResponse {
    this.apply {
        return SavedSpUserResponse(
            userId = userId,
            fullName = fullName,
            uid = uid,
            anonymous = anonymous,
            absoluteMobile = absoluteMobile,
            email = email,
            countryCode = countryCode,
            notificationToken = notificationToken,
            notificationTokenProvider = notificationTokenProvider,
            createdAt = DateUtils.getEpoch(createdAt),
            handle = handle,
            dp = getMediaDetailsForDP(),
            coverImage = getMediaDetailsForCoverImage(),
            verified = verified,

            contactVisible = contactVisible,

        )
    }
}

fun SpUser.toSpUserPublicMiniDataResponse(): SpUserPublicMiniDataResponse {
    this.apply {
        val isContactVisible = false
        return SpUserPublicMiniDataResponse(
            userId = userId,
            fullName = fullName,
            uid = uid,
            createdAt = DateUtils.getEpoch(createdAt),
            handle = handle,
            email = if (isContactVisible) email else "",
            absoluteMobile = if (isContactVisible) absoluteMobile else "",
            dp = getMediaDetailsForDP(),
            coverImage = getMediaDetailsForCoverImage(),
            verified = verified,

            contactVisible = contactVisible,
        )
    }
}
