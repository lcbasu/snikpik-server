package com.server.dk.entities

import com.server.dk.enums.ProductStatus
import com.server.dk.enums.ProductUnit
import com.server.common.model.MediaDetails
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.server.common.entities.Auditable
import com.server.common.entities.User
import javax.persistence.*

@Entity
class Product : Auditable() {
    @Id
    @Column(unique = true)
    var id: String = ""

    var title: String = ""

    var mediaDetails: String = "" // MediaDetails object -> Multiple Images or videos

    @Enumerated(EnumType.STRING)
    var productStatus: ProductStatus = ProductStatus.ACTIVE

    @Enumerated(EnumType.STRING)
    @Column(updatable = false) // You can not update the product unit after this is published
    var productUnit: ProductUnit = ProductUnit.PIECE

    var unitQuantity: Long = 0 // Like 100 ProductUnit.GRAM

    var description: String? = null

    var taxPerUnitInPaisa: Long = 0 // If zero then tax is included otherwise excluded
    var pricePerUnitInPaisa: Long = 0

    var originalPricePerUnitInPaisa: Long = 0
    var sellingPricePerUnitInPaisa: Long = 0

    var minOrderUnitCount: Long = 0

    var totalUnitInStock: Long = 0

    var totalOrderAmountInPaisa: Long? = 0
    var totalViewsCount: Long? = 0
    var totalClicksCount: Long? = 0

    var totalVariantsViewsCount: Long? = 0
    var totalVariantsClicksCount: Long? = 0

    var totalUnitsOrdersCount: Long? = 0
    var totalOrdersCount: Long? = 0

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
//    @MapsId("company_id")
    @JoinColumn(name = "company_id")
    var company: Company? = null;

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "added_by_user_id")
    var addedBy: User? = null;

    // Sets of referenced objets
    @OneToMany
    @JoinColumn(name = "product_id")
    var productVariants: Set<ProductVariant> = emptySet()

    @OneToMany
    @JoinColumn(name = "product_id")
    var productCollections: Set<ProductCollection> = emptySet()
}

fun Product.getMediaDetails(): MediaDetails {
    this.apply {
        return jacksonObjectMapper().readValue(mediaDetails, MediaDetails::class.java)
    }
}

