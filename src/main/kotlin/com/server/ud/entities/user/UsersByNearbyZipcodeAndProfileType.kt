package com.server.ud.entities.user

import com.server.common.enums.ProfileType
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

@Table("users_by_nearby_zipcode_and_profile_type")
data class UsersByNearbyZipcodeAndProfileType (

    @PrimaryKeyColumn(name = "zipcode", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var zipcode: String,

    @PrimaryKeyColumn(name = "profile_type", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
    var profileType: ProfileType,

    @PrimaryKeyColumn(name = "user_id", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
    var userId: String,

    @Column("original_zipcode")
    var originalZipcode: String,
)


@Table("users_by_nearby_zipcode_and_profile_type_tracker")
data class UsersByNearbyZipcodeAndProfileTypeTracker (

    @PrimaryKeyColumn(name = "user_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var userId: String,

    @PrimaryKeyColumn(name = "zipcode", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    var zipcode: String,

    @PrimaryKeyColumn(name = "profile_type", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
    var profileType: ProfileType,

    @Column("original_zipcode")
    var originalZipcode: String,
)
fun UsersByNearbyZipcodeAndProfileType.toUsersByNearbyZipcodeAndProfileTypeTracker(): UsersByNearbyZipcodeAndProfileTypeTracker {
    this.apply {
        return UsersByNearbyZipcodeAndProfileTypeTracker(
            userId = this.userId,
            zipcode = this.zipcode,
            profileType = this.profileType,
            originalZipcode = this.originalZipcode
        )
    }
}

fun UsersByNearbyZipcodeAndProfileTypeTracker.toUsersByNearbyZipcodeAndProfileType(): UsersByNearbyZipcodeAndProfileType {
    this.apply {
        return UsersByNearbyZipcodeAndProfileType(
            userId = this.userId,
            zipcode = this.zipcode,
            profileType = this.profileType,
            originalZipcode = this.originalZipcode
        )
    }
}
