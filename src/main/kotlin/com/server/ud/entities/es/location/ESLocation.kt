package com.server.ud.entities.es.location

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.server.common.utils.DateUtils
import com.server.ud.enums.LocationFor
import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.*
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
@Document(indexName = "locations")
@JsonIgnoreProperties(ignoreUnknown = true)
class ESLocation (

    @Id
    var locationId: String,

    @Field(type = FieldType.Date_Nanos)
    var createdAt: Long = DateUtils.getCurrentTimeInEpoch(),

    @Field(type = FieldType.Keyword)
    var userId: String,

    @Field(type = FieldType.Keyword)
    var locationFor: LocationFor,

    @Field(type = FieldType.Keyword)
    var zipcode: String? = null,

    @Field(type = FieldType.Keyword)
    var googlePlaceId: String? = null,

    @MultiField(
        mainField = Field(type = FieldType.Text, fielddata = true),
        otherFields = [InnerField(suffix = "verbatim", type = FieldType.Keyword)]
    )
    val name: String? = null,

    @Field(type = FieldType.Double)
    val lat: Double? = null,

    @Field(type = FieldType.Double)
    val lng:Double? = null,

    @GeoPointField
    val geoPoint: GeoPoint? = null,
)

