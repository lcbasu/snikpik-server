package com.server.ud.entities.user

import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Indexed
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

@Table("nearby_zipcodes_by_zipcode")
class NearbyZipcodesByZipcode {

    @PrimaryKeyColumn(name = "zipcode", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var zipcode: String? = null

    @Indexed
    @PrimaryKeyColumn(name = "nearby_zipcode", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    var nearbyZipcode: String? = null
}

