package com.server.ud.entities.location

import com.server.ud.entities.post.Post
import com.server.ud.enums.LocationFor
import com.server.ud.enums.PostType
import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.Indexed
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import org.springframework.data.elasticsearch.core.geo.GeoPoint
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
class Location (

    @PrimaryKeyColumn(name = "location_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var locationId: String,

    @PrimaryKeyColumn(name = "created_at", ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    var createdAt: Instant = Instant.now(),

    @PrimaryKeyColumn(name = "user_id", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
    var userId: String,

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

fun Location.getGeoPointData(): GeoPoint? {
    this.apply {
        if (lat != null && lng != null) {
            return GeoPoint(lat, lng)
        }
        return null
    }
}

