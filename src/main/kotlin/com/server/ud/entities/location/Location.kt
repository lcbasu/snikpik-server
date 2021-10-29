package com.server.ud.entities.location

import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.Indexed
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.time.Instant

/**
 * The response from Google Maps Autocomplete
 *
 * will fill the following columns
 *
 * id -> Auto generated
 * name -> Display name from Geo Response
 * lat -> Latitude from Geo Response
 * lng -> Longitude name from Geo Response
 * zipcode -> Zipcode from Geo Response
 * googlePlacesId -> Unique Google Place ID
 *
 * */
@Table("locations")
class Location {

    @PrimaryKeyColumn(name = "location_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var locationId: String? = null

    @PrimaryKeyColumn(name = "created_at", ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    var createdAt: Instant = Instant.now()

    @Indexed
    @PrimaryKeyColumn(name = "zipcode", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
    var zipcode: String? = null

    @Indexed
    @PrimaryKeyColumn(name = "google_place_id", ordinal = 3, type = PrimaryKeyType.CLUSTERED)
    var googlePlaceId: String? = null

    @Column
    val name: String? = null

    @Column
    val lat: Double? = null

    @Column
    val lng:Double? = null
}

