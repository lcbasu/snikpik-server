package com.dukaankhata.server.entities

import com.dukaankhata.server.enums.ProductStatus
import com.dukaankhata.server.enums.ProductUnit
import javax.persistence.*

@Entity
class Product : Auditable() {
    @Id
    var id: String = ""
    var title: String = ""

    var mediaDetails: String = "" // MediaDetails object -> Multiple Images or videos

    @Enumerated(EnumType.STRING)
    var productStatus: ProductStatus = ProductStatus.ACTIVE

    @Enumerated(EnumType.STRING)
    @Column(updatable = false) // You can not update the product unit after this is published
    var productUnit: ProductUnit = ProductUnit.KG

    val taxPerUnitInPaisa: Long = 0 // If zero then tax is included otherwise excluded
    val pricePerUnitInPaisa: Long = 0

    val totalUnitInStock: Long = 0

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @MapsId("company_id")
    @JoinColumn(name = "company_id")
    var company: Company? = null;

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "added_by_user_id")
    var addedBy: User? = null;
}
