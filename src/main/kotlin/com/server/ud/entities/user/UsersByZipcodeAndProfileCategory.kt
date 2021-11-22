package com.server.ud.entities.user

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.server.common.enums.ProfileCategory
import com.server.common.enums.ProfileType
import com.server.common.enums.UserPositionInMarketplace
import com.server.dk.model.MediaDetailsV2
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

/**
 *
 * We will have different professionals and suppliers for different zipcode
 *
 * */
@Table("users_by_zipcode_and_profile_category")
class UsersByZipcodeAndProfileCategory (

    @PrimaryKeyColumn(name = "zipcode", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var zipcode: String,

    @PrimaryKeyColumn(name = "profile_category", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
    var profileCategory: ProfileCategory,

    @PrimaryKeyColumn(name = "profile_type", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
    var profileType: ProfileType,

    @PrimaryKeyColumn(name = "position", ordinal = 3, type = PrimaryKeyType.CLUSTERED)
    var position: UserPositionInMarketplace,

    @Column("user_id")
    var userId: String,

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

    // The name where this zipcode was saved
    @Column("user_location_name")
    val userLocationName: String? = null,
)


fun UsersByZipcodeAndProfileCategory.getMediaDetailsForDP(): MediaDetailsV2? {
    this.apply {
        return try {
            jacksonObjectMapper().readValue(dp, MediaDetailsV2::class.java)
        } catch (e: Exception) {
            null
        }
    }
}

fun UsersByZipcodeAndProfileCategory.getProfiles(): Set<ProfileType> {
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
