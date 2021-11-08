package com.server.ud.entities.es.post

import com.server.common.enums.ProfileType
import com.server.common.utils.DateUtils
import com.server.ud.enums.PostType
import com.server.ud.model.HashTagData
import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import org.springframework.data.elasticsearch.annotations.GeoPointField
import org.springframework.data.elasticsearch.core.geo.GeoPoint
import java.time.Instant

@Document(indexName = "posts")
class ESPost (

    @Id
    var postId: String,

    @Field(type = FieldType.Date_Nanos)
    var createdAt: Instant = DateUtils.getInstantNow(),

    @Field(type = FieldType.Keyword)
    var userId: String,

    @Field(type = FieldType.Keyword)
    var postType: PostType,

    @Field(type = FieldType.Text)
    var title: String? = null,

    @Field(type = FieldType.Text)
    var description: String? = null,

    @Field(type = FieldType.Text)
    var media: String? = null, // MediaDetailsV2

    @Field(type = FieldType.Nested, includeInParent = true)
    var tags: List<HashTagData> = emptyList(), // List of HashTagsList

//    @Field(type = FieldType.Nested, includeInParent = true)
//    var categories: Set<String>, //  List of CategoryV2

    @Field(type = FieldType.Keyword)
    var locationId: String? = null,

    @Field(type = FieldType.Keyword)
    var zipcode: String? = null,

    @Field(type = FieldType.Text)
    val locationName: String? = null,

    @Field(type = FieldType.Double)
    val locationLat: Double? = null,

    @Field(type = FieldType.Double)
    val locationLng: Double? = null,

    @GeoPointField
    val geoPoint: GeoPoint? = null,

    @Field(type = FieldType.Text)
    var userHandle: String? = null,

    @Field(type = FieldType.Text)
    var userName: String? = null,

    @Field(type = FieldType.Text)
    var userMobile: String? = null,

    @Field(type = FieldType.Text)
    var userCountryCode: String? = null,

    @Field(type = FieldType.Keyword)
    var userProfile: ProfileType? = null,
)


