package com.server.ud.entities.user

import com.server.common.enums.ProfileType
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

/**
 *
 * We will have different professionals and suppliers for different zipcode
 *
 * */
@Table("users_by_zipcode_and_profile_type")
class UsersByZipcodeAndProfileType (

    @PrimaryKeyColumn(name = "zipcode", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var zipcode: String,

    @PrimaryKeyColumn(name = "profile_type", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
    var profileType: ProfileType,

    @PrimaryKeyColumn(name = "user_id", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
    var userId: String,
)
