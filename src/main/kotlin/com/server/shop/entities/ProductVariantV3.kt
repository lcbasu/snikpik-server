package com.server.shop.entities

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.server.common.entities.Auditable
import com.server.common.model.MediaDetailsV2
import com.server.common.utils.DateUtils
import com.server.shop.enums.ProductVariantStatusV3
import com.server.shop.model.SpecificationInfoList
import com.server.shop.model.VariantInfoV3List
import com.server.shop.model.VariantProperties
import javax.persistence.*

@Entity(name = "product_variant_v3")
class ProductVariantV3 : Auditable() {
    @Id
    @Column(unique = true)
    var id: String = ""

    var title: String = ""
    var description: String = ""

    var mediaDetails: String = ""

    var variantInfos: String = "" // VariantInfoV3List object with single entry for each variant type

    var properties: String = "" // ProductProperties object

    var specification: String = "" // SpecificationInfoList object

    var unboxTakesCommissionPercentage: Double = 5.00
    var unboxTakesMaxCommissionInPaisa: Long = 5000000 // We allow selling products at max 10 lac rupees

    var unboxGivesCommissionPercentage: Double = 10.00
    var unboxGivesMaxCommissionInPaisa: Long = 5000000 // We give a maximum of Rs 50K as commission
    var managedByUnbox: Boolean = false

    @Enumerated(EnumType.STRING)
    var status: ProductVariantStatusV3 = ProductVariantStatusV3.PENDING_APPROVAL

    var viewInRoomAllowed: Boolean = false

    var categories: String = "" // AllProductCategories as String

    // Delivery
    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "shipped_from_address_id")
    var shippedFrom: AddressV3? = null; // Must have lat = "" lng and zipcode
    var maxDeliveryDistanceInKm: Int = 1000
    var deliversOverIndia: Boolean = true

    var maxDeliveryTimeInSeconds: Long = DateUtils.convertDaysToSeconds(13) // Keep it as 13 days for now

    var replacementAcceptable: Boolean = true
    var returnAcceptable: Boolean = true

    var codAvailable: Boolean = true

    var mrpPerUnitInPaisa: Long = 0 // Rs 100
    var sellingPricePerUnitInPaisa: Long = 0 // Rs 90 -> Automatically teh discount is 10%

    var minOrderUnitCount: Long = 1
    var maxOrderPerUser: Long = -1

    // Difference between totalUnitInStock and totalSoldUnits gives the total available units
    var totalUnitInStock: Long = -1 // Total number of units in stock -> Always keep increasing. never reset. -1 -> Unlimited
    var totalSoldUnits: Long = 0 // Total number of units that was sold -> Always keep increasing, never reset
    var totalSoldAmountInPaisa: Long = 0 // Total number of units that was sold -> Always keep increasing, never reset
    var totalOrdersCount: Long? = 0

    // This CascadeType array is to fix Multiple representations of the same entity
    @ManyToOne(cascade = [CascadeType.PERSIST, CascadeType.DETACH, CascadeType.REFRESH, CascadeType.REMOVE], fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    var product: ProductV3? = null;

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    var brand: Brand? = null;

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    var company: CompanyV3? = null;

    @ManyToOne(cascade = [CascadeType.PERSIST, CascadeType.DETACH, CascadeType.REFRESH, CascadeType.REMOVE], fetch = FetchType.LAZY)
    @JoinColumn(name = "added_by_user_id")
    var addedBy: UserV3? = null;
}

fun ProductVariantV3.getMediaDetailsV2(): MediaDetailsV2 {
    this.apply {
        return try {
            jacksonObjectMapper().readValue(mediaDetails, MediaDetailsV2::class.java)
        } catch (e: Exception) {
            MediaDetailsV2(emptyList())
        }
    }
}

fun ProductVariantV3.getVariantInfoV3List(): VariantInfoV3List {
    this.apply {
        return try {
            jacksonObjectMapper().readValue(variantInfos, VariantInfoV3List::class.java)
        } catch (e: Exception) {
            VariantInfoV3List(emptyMap())
        }
    }
}

fun ProductVariantV3.getVariantProperties(): VariantProperties {
    this.apply {
        return try {
            jacksonObjectMapper().readValue(properties, VariantProperties::class.java)
        } catch (e: Exception) {
            VariantProperties(emptyList())
        }
    }
}

fun ProductVariantV3.getSpecificationInfoList(): SpecificationInfoList {
    this.apply {
        return try {
            jacksonObjectMapper().readValue(specification, SpecificationInfoList::class.java)
        } catch (e: Exception) {
            SpecificationInfoList(emptyList())
        }
    }
}


fun ProductVariantV3.getAllProductCategories(): AllProductCategories {
    this.apply {
        return try {
            jacksonObjectMapper().readValue(categories, AllProductCategories::class.java)
        } catch (e: Exception) {
            AllProductCategories(emptySet())
        }
    }
}

