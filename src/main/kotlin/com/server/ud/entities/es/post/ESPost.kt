package com.server.ud.entities.es.post

import com.server.ud.enums.PostType
import com.server.ud.model.HashTagData
import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.*
import org.springframework.data.elasticsearch.core.geo.GeoPoint
import java.time.Instant

@Document(indexName = "posts")
class ESPost (

    @Id
    var postId: String,

    @Field(type = FieldType.Date_Nanos)
    var createdAt: Instant = Instant.now(),

    @Field(type = FieldType.Keyword)
    var userId: String,

    @Field(type = FieldType.Text)
    var postType: PostType,

    @MultiField(
        mainField = Field(type = FieldType.Text, fielddata = true),
        otherFields = [InnerField(suffix = "verbatim", type = FieldType.Keyword)]
    )
    var title: String? = null,

    @MultiField(
        mainField = Field(type = FieldType.Text, fielddata = true),
        otherFields = [InnerField(suffix = "verbatim", type = FieldType.Keyword)]
    )
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

    @MultiField(
        mainField = Field(type = FieldType.Text, fielddata = true),
        otherFields = [InnerField(suffix = "verbatim", type = FieldType.Keyword)]
    )
    val locationName: String? = null,

    @Field(type = FieldType.Double)
    val locationLat: Double? = null,

    @Field(type = FieldType.Double)
    val locationLng: Double? = null,

    @GeoPointField
    val geoPoint: GeoPoint? = null,
)


