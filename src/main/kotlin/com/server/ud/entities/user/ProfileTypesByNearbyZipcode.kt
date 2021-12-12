package com.server.ud.entities.user

import com.server.common.enums.ProfileCategory
import com.server.common.enums.ProfileType
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

@Table("profile_types_by_nearby_zipcode")
data class ProfileTypesByNearbyZipcode (

    @PrimaryKeyColumn(name = "zipcode", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var zipcode: String,

    @PrimaryKeyColumn(name = "profile_category", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
    var profileCategory: ProfileCategory,

    @PrimaryKeyColumn(name = "profile_type", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
    var profileType: ProfileType,

    @Column("original_zipcode")
    var originalZipcode: String,
)
