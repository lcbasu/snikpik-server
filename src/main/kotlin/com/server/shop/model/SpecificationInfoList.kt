package com.server.shop.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.javafaker.Faker
import com.server.common.model.MediaDetailsV2
import com.server.common.model.sampleMedia
import com.server.shop.enums.MediaOrientation
import com.server.shop.enums.SpecificationType
import kotlin.random.Random

fun SpecificationInfoList.convertToString(): String {
    this.apply {
        return jacksonObjectMapper().writeValueAsString(this)
    }
}

data class SpecificationInfoList (
    val specifications: List<SpecificationInfoModel>
)

interface SpecificationInfoBaseModel {
    var type: SpecificationType
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
    JsonSubTypes.Type(value = MediaDetailedSpecificationInfo::class, name = "IMAGE_OR_VIDEO_BASED_DESCRIPTION"),
    JsonSubTypes.Type(value = KeyPaiDetailedSpecificationInfo::class, name = "KEY_PAIR_DESCRIPTION"),
    JsonSubTypes.Type(value = CarouselSpecificationInfo::class, name = "MEDIA_CAROUSEL"),
    JsonSubTypes.Type(value = ProductDescriptionMediaSpecificationInfo::class, name = "PRODUCT_DESCRIPTION_MEDIA_CAROUSEL"),
)
sealed class SpecificationInfoModel : SpecificationInfoBaseModel

data class ProductDetailsModel (
    val title: String,
    val description: String,
    val mediaDetails: MediaDetailsV2,
    val mediaOrientation: MediaOrientation
)

// Example
// https://www.amazon.in/dp/B085WV64Y7/?pf_rd_r=EFG1F70FX8RD19RA761Q&pf_rd_p=d2e94e7e-e621-48cb-ab2b-8896f3f07ad9&pd_rd_r=233387bd-f101-453a-b5c4-28b7e8984313&pd_rd_w=u7mYE&pd_rd_wg=hrWK2&ref_=pd_gw_unk
@JsonTypeName("IMAGE_OR_VIDEO_BASED_DESCRIPTION")
@JsonIgnoreProperties(ignoreUnknown = true)
data class MediaDetailedSpecificationInfo (
    val details: List<ProductDetailsModel>,

    override var type: SpecificationType = SpecificationType.IMAGE_OR_VIDEO_BASED_DESCRIPTION,
    override var name: String? = null,
    override var description: String? = null,
    override var mediaDetails: MediaDetailsV2? = null,
): SpecificationInfoModel() {}

@JsonTypeName("KEY_PAIR_DESCRIPTION")
@JsonIgnoreProperties(ignoreUnknown = true)
data class KeyPaiDetailedSpecificationInfo (
    val kvPair: Map<String, String>,

    override var type: SpecificationType = SpecificationType.KEY_PAIR_DESCRIPTION,
    override var name: String? = null,
    override var description: String? = null,
    override var mediaDetails: MediaDetailsV2? = null,
): SpecificationInfoModel() {}


// Check figma design
@JsonTypeName("MEDIA_CAROUSEL")
@JsonIgnoreProperties(ignoreUnknown = true)
data class CarouselSpecificationInfo (
    val carouselMedia: MediaDetailsV2, // The media is already ordered

    override var type: SpecificationType = SpecificationType.MEDIA_CAROUSEL,
    override var name: String? = null,
    override var description: String? = null,
    override var mediaDetails: MediaDetailsV2? = null,
): SpecificationInfoModel() {}

@JsonTypeName("PRODUCT_DESCRIPTION_MEDIA_CAROUSEL")
@JsonIgnoreProperties(ignoreUnknown = true)
data class ProductDescriptionMediaSpecificationInfo (
    val title: String,
    val carouselMedia: MediaDetailsV2,

    override var type: SpecificationType = SpecificationType.PRODUCT_DESCRIPTION_MEDIA_CAROUSEL,
    override var name: String? = null,
    override var description: String? = null,
    override var mediaDetails: MediaDetailsV2? = null,
): SpecificationInfoModel() {}

fun getSampleSpecificationInfoList(): SpecificationInfoList {
    val faker = Faker()
    return SpecificationInfoList(
        listOf(ProductDescriptionMediaSpecificationInfo(
            title = faker.book().author(),
            carouselMedia = sampleMedia.shuffled()[Random.nextInt(sampleMedia.size)]
        ))
    )
}
