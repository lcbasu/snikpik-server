package com.server.ud.entities.user

import com.server.common.enums.ProfileCategory
import com.server.common.enums.ProfileType
import com.server.common.enums.UserPositionInMarketplace
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

@Table("pointer_for_users_in_marketplace")
class PointerForUsersInMarketplace (

    @PrimaryKeyColumn(name = "profile_category", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var profileCategory: ProfileCategory,

    @PrimaryKeyColumn(name = "profile_type", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
    var profileType: ProfileType,

    @PrimaryKeyColumn(name = "zipcode", ordinal = 2, type = PrimaryKeyType.PARTITIONED)
    var zipcode: String,

    @Column("last_position")
    var lastPosition: UserPositionInMarketplace,
)

