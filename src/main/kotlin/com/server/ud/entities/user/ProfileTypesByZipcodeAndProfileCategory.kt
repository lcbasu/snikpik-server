package com.server.ud.entities.user

import com.server.common.enums.ProfileCategory
import com.server.common.enums.ProfileType
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

@Table("profile_types_by_zipcode_and_profile_category")
class ProfileTypesByZipcodeAndProfileCategory (

    @PrimaryKeyColumn(name = "zipcode", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var zipcode: String,

    @PrimaryKeyColumn(name = "profile_category", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
    var profileCategory: ProfileCategory,

    @PrimaryKeyColumn(name = "profile_type", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
    var profileType: ProfileType,
)
