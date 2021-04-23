package com.dukaankhata.server.entities

import com.dukaankhata.server.enums.ProductStatus
import com.dukaankhata.server.enums.ProductUnit
import com.dukaankhata.server.model.MediaDetails
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
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
    var productUnit: ProductUnit = ProductUnit.KG

    var taxPerUnitInPaisa: Long = 0 // If zero then tax is included otherwise excluded
    var pricePerUnitInPaisa: Long = 0
    var minOrderUnitCount: Long = 0

    var totalUnitInStock: Long = 0

    var totalOrderAmountInPaisa: Long? = 0
    var totalViewsCount: Long? = 0
    var totalUnitsOrdersCount: Long? = 0

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
//    @MapsId("company_id")
    @JoinColumn(name = "company_id")
    var company: Company? = null;

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "added_by_user_id")
    var addedBy: User? = null;
}

fun Product.getMediaDetails(): MediaDetails {
    this.apply {
        return jacksonObjectMapper().readValue(mediaDetails, MediaDetails::class.java)
    }
}

