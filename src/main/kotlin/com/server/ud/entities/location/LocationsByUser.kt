package com.server.ud.entities.location

import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.Indexed
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.time.Instant

// All the locations that the user has travelled to or modified
@Table("locations_by_user")
class LocationsByUser {

    @PrimaryKeyColumn(name = "user_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var userId: String? = null

    @PrimaryKeyColumn(name = "created_at", ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    var createdAt: Instant = Instant.now()

    @Indexed
    @PrimaryKeyColumn(name = "location_id", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
    var locationId: String? = null

    @Indexed
    @PrimaryKeyColumn(name = "zipcode", ordinal = 3, type = PrimaryKeyType.CLUSTERED)
    var zipcode: String? = null

    @Indexed
    @PrimaryKeyColumn(name = "google_place_id", ordinal = 4, type = PrimaryKeyType.CLUSTERED)
    var googlePlaceId: String? = null

    @Column
    val name: String? = null

    @Column
    val lat: Double? = null

    @Column
    val lng:Double? = null
}

