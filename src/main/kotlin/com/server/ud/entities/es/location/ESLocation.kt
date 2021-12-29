package com.server.ud.entities.es.location

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.server.common.utils.DateUtils
import com.server.ud.enums.LocationFor
import org.springframework.data.annotation.Id
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import org.springframework.data.elasticsearch.annotations.GeoPointField
import org.springframework.data.elasticsearch.core.geo.GeoPoint

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

    @Field(type = FieldType.Text)
    val name: String? = null,

    @Field(type = FieldType.Double)
    val lat: Double? = null,

    @Field(type = FieldType.Double)
    val lng:Double? = null,

    @GeoPointField
    val geoPoint: GeoPoint? = null,

    @Field(type = FieldType.Text)
    val locality: String? = null,

    @Field(type = FieldType.Text)
    val subLocality: String? = null,

    @Field(type = FieldType.Text)
    val route: String? = null,

    @Field(type = FieldType.Text)
    val city: String? = null,

    @Field(type = FieldType.Text)
    val state: String? = null,

    @Field(type = FieldType.Text)
    val country: String? = null,

    @Field(type = FieldType.Text)
    val countryCode: String? = null,

    @Field(type = FieldType.Text)
    val completeAddress: String? = null,
)

