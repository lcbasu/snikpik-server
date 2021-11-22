package com.server.ud.entities.location

import com.server.common.utils.DateUtils
import com.server.ud.enums.LocationFor
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.CassandraType
import org.springframework.data.cassandra.core.mapping.Column
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

    @Column("created_at")
    @CassandraType(type = CassandraType.Name.TIMESTAMP)
    var createdAt: Instant = DateUtils.getInstantNow(),

    @Column("user_id")
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

