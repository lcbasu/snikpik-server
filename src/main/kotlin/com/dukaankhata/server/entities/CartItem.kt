package com.dukaankhata.server.entities

import javax.persistence.*

@Entity
class CartItem : Auditable() {
    @Id
    @Column(unique = true)
    var id: String = ""

    var totalUnits: Long = 0

    // Copying these 2 fields from product as the product prices can change in future
    var taxPerUnitInPaisa: Long = 0
    var pricePerUnitInPaisa: Long = 0

    var totalTaxInPaisa: Long = 0 // totalUnits * product.taxPerUnitInPaisa
    var totalPriceWithoutTaxInPaisa: Long = 0 // totalUnits * product.pricePerUnitInPaisa

    // This cart item will ALWAYS belong to a cart
    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "product_order_id")
    var productOrder: ProductOrder? = null;

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    var product: Product? = null;

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "product_variant_id")
    var productVariant: ProductVariant? = null;

    // Keeping company:Company & addedBy:User  reference in all the models
    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    var company: Company? = null;

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "added_by_user_id")
    var addedBy: User? = null;
}
