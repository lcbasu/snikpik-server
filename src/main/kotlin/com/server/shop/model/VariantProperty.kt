package com.server.shop.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.server.common.model.MediaDetailsV2
import com.server.shop.enums.ProductPropertyType


fun VariantProperties.convertToString(): String {
    this.apply {
        return jacksonObjectMapper().writeValueAsString(this)
    }
}

// Can be more than one property value for same type
// Example: Having Charger, sticker, carrying case as ADDITIONAL_UNITS
data class VariantProperties (
    val properties: List<ProductPropertyModel>,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AdditionalItem (
    val name: String,
    val description: String? = null,
    val mediaDetails: MediaDetailsV2? = null,
)

interface ProductPropertyBaseModel {
    var type: ProductPropertyType
    var name: String?
    val description: String?
    var mediaDetails: MediaDetailsV2?
}

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = AdditionalItemsProperty::class, name = "ADDITIONAL_UNITS"),
    JsonSubTypes.Type(value = WarrantyProperty::class, name = "WARRANTY"),
    JsonSubTypes.Type(value = WarrantyTillDateProperty::class, name = "WARRANTY_TILL_DATE"),
    JsonSubTypes.Type(value = WarrantyForNextMonthProperty::class, name = "WARRANTY_FOR_NEXT_MONTHS"),
)
sealed class ProductPropertyModel : ProductPropertyBaseModel

@JsonTypeName("ADDITIONAL_UNITS")
@JsonIgnoreProperties(ignoreUnknown = true)
data class AdditionalItemsProperty(
    val items: List<AdditionalItem>,
    override var type: ProductPropertyType = ProductPropertyType.ADDITIONAL_UNITS,
    override var name: String? = null,
    override var description: String? = null,
    override var mediaDetails: MediaDetailsV2? = null,
) : ProductPropertyModel() {

}

@JsonTypeName("WARRANTY")
@JsonIgnoreProperties(ignoreUnknown = true)
data class WarrantyProperty(
    val warrantyDescription: String?,
    override var type: ProductPropertyType = ProductPropertyType.WARRANTY,
    override var name: String? = null,
    override var description: String? = null,
    override var mediaDetails: MediaDetailsV2? = null,
) : ProductPropertyModel() {
}

@JsonTypeName("WARRANTY_TILL_DATE")
@JsonIgnoreProperties(ignoreUnknown = true)
data class WarrantyTillDateProperty (
    val tillDate: Long,
    override var type: ProductPropertyType = ProductPropertyType.WARRANTY_TILL_DATE,
    override var name: String? = null,
    override var description: String? = null,
    override var mediaDetails: MediaDetailsV2? = null,
) : ProductPropertyModel() {
}

@JsonTypeName("WARRANTY_FOR_NEXT_MONTHS")
@JsonIgnoreProperties(ignoreUnknown = true)
data class WarrantyForNextMonthProperty (
    val startDate: Long,
    val forMonthsFromStartDate: Int,
    override var type: ProductPropertyType = ProductPropertyType.WARRANTY_FOR_NEXT_MONTHS,
    override var name: String? = null,
    override var description: String? = null,
    override var mediaDetails: MediaDetailsV2? = null,
) : ProductPropertyModel() {
}

fun getSampleVariantProperties(): VariantProperties {
    return VariantProperties(
        listOf(
            AdditionalItemsProperty(
                listOf(
                    AdditionalItem (
                        name = "Screws + Charger",
                        description = "",)
                )
            ),
            WarrantyProperty(
                warrantyDescription = "1 year warranty"
            )
        )
    )
}
