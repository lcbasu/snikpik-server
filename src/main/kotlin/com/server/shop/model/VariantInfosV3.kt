package com.server.shop.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.server.common.model.MediaDetailsV2
import com.server.shop.enums.DimensionUnits
import com.server.shop.enums.ProductPropertyType
import com.server.shop.enums.VariantInfoTypeV3
import com.server.shop.enums.WeightUnits

// Only one info per type
data class VariantInfoV3List (
    val infos: Map<VariantInfoTypeV3, VariantInfoV3Model>
)

interface VariantInfoV3BaseModel {
    var type: VariantInfoTypeV3
    var name: String?
    var description: String?
    var mediaDetails: MediaDetailsV2?
}

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = DimensionVariantInfo::class, name = "DIMENSION"),
    JsonSubTypes.Type(value = WeightVariantInfo::class, name = "WEIGHT"),
    JsonSubTypes.Type(value = ColorVariantInfo::class, name = "COLOR"),
    JsonSubTypes.Type(value = MaterialVariantInfo::class, name = "MATERIAL"),
)
sealed class VariantInfoV3Model : VariantInfoV3BaseModel


@JsonTypeName("DIMENSION")
@JsonIgnoreProperties(ignoreUnknown = true)
data class DimensionVariantInfo (
    val unit: DimensionUnits = DimensionUnits.CM,
    val length: Double,
    val breadth: Double,
    val height: Double,

    override var type: VariantInfoTypeV3 = VariantInfoTypeV3.DIMENSION,
    override var name: String? = null,
    override var description: String? = null,
    override var mediaDetails: MediaDetailsV2? = null,
) : VariantInfoV3Model() {}

@JsonTypeName("WEIGHT")
@JsonIgnoreProperties(ignoreUnknown = true)
data class WeightVariantInfo (
    val unit: WeightUnits = WeightUnits.KG,
    val weight: Double,

    override var type: VariantInfoTypeV3 = VariantInfoTypeV3.WEIGHT,
    override var name: String? = null,
    override var description: String? = null,
    override var mediaDetails: MediaDetailsV2? = null,
) : VariantInfoV3Model() {}

@JsonTypeName("COLOR")
@JsonIgnoreProperties(ignoreUnknown = true)
data class ColorVariantInfo (
    val colorCode: String,

    override var type: VariantInfoTypeV3 = VariantInfoTypeV3.COLOR,
    override var name: String? = null,
    override var description: String? = null,
    override var mediaDetails: MediaDetailsV2? = null,
) : VariantInfoV3Model() {}

@JsonTypeName("MATERIAL")
@JsonIgnoreProperties(ignoreUnknown = true)
data class MaterialVariantInfo (
    val textureCode: String? = null,

    override var type: VariantInfoTypeV3 = VariantInfoTypeV3.MATERIAL,
    override var name: String? = null,
    override var description: String? = null,
    override var mediaDetails: MediaDetailsV2? = null,
) : VariantInfoV3Model() {}

fun VariantInfoV3List.convertToString(): String {
    this.apply {
        return jacksonObjectMapper().writeValueAsString(this)
    }
}

fun getSampleVariantInfoV3List(): VariantInfoV3List {
    return VariantInfoV3List(
        infos = mapOf(
            VariantInfoTypeV3.DIMENSION to DimensionVariantInfo(
                unit = DimensionUnits.CM,
                length = 10.5,
                breadth = 10.5,
                height = 10.5
            ),
            VariantInfoTypeV3.WEIGHT to WeightVariantInfo(
                unit = WeightUnits.KG,
                weight = 10.0
            ),
            VariantInfoTypeV3.COLOR to ColorVariantInfo(
                colorCode = "red"
            ),
            VariantInfoTypeV3.MATERIAL to MaterialVariantInfo(
                textureCode = "Leather"
            )
        )
    )
}
