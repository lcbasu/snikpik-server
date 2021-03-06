package com.server.dk.entities

import VariantInfos
import com.server.common.model.MediaDetails
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.server.common.entities.Auditable
import com.server.common.entities.User
import javax.persistence.*

@Entity
class ProductVariant : Auditable() {
    @Id
    @Column(unique = true)
    var id: String = ""

    var title: String = ""

    var taxPerUnitInPaisa: Long = 0 // If zero then tax is included otherwise excluded

    var originalPricePerUnitInPaisa: Long = 0
    var sellingPricePerUnitInPaisa: Long = 0

    var totalViewsCount: Long? = 0
    var totalClicksCount: Long? = 0
    var totalUnitInStock: Long = 0
    var totalOrderAmountInPaisa: Long? = 0
    var totalUnitsOrdersCount: Long? = 0
    var totalOrdersCount: Long? = 0

    var mediaDetails: String = "" // MediaDetails object -> Multiple Images or videos
    var variantInfos: String = "" // VariantInfos object

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    var product: Product? = null;

    // Keeping company:Company & addedBy:User  reference in all the models
    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    var company: Company? = null;

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "added_by_user_id")
    var addedBy: User? = null;
}

fun ProductVariant.getMediaDetails(): MediaDetails {
    this.apply {
        return jacksonObjectMapper().readValue(mediaDetails, MediaDetails::class.java)
    }
}

fun ProductVariant.getVariantInfos(): VariantInfos? {
    this.apply {
        return try {
            jacksonObjectMapper().readValue(variantInfos, VariantInfos::class.java)
        } catch (e: Exception) {
            null
        }

    }
}

