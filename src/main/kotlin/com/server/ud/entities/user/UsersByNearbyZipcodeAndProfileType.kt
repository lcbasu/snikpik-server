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
