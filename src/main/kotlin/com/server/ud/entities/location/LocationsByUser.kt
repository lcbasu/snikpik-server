package com.server.ud.entities.location

import com.server.common.utils.DateUtils
import com.server.ud.enums.LocationFor
import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.time.Instant

// All the locations that the user has travelled to or modified
@Table("locations_by_user")
class LocationsByUser (

    @PrimaryKeyColumn(name = "user_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var userId: String? = null,

    @PrimaryKeyColumn(name = "for_date", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
    var forDate: Instant = DateUtils.getInstantToday(),

    @PrimaryKeyColumn(name = "created_at", ordinal = 2, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    var createdAt: Instant = DateUtils.getInstantNow(),

    @PrimaryKeyColumn(name = "location_id", ordinal = 3, type = PrimaryKeyType.CLUSTERED)
    var locationId: String? = null,

    @Column("location_for")
    var locationFor: LocationFor,

    @Column("zipcode")
    var zipcode: String? = null,

    @Column("google_place_id")
    var googlePlaceId: String? = null,

    @Column
    val name: String? = null,

    @Column
    val lat: Double? = null,

    @Column
    val lng:Double? = null,
)

