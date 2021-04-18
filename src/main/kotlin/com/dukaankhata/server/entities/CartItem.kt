package com.dukaankhata.server.entities

import javax.persistence.*

@Entity
class CartItem : Auditable() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    val totalUnits: Long = 0

    // Copying these 2 fields from product as the product prices can change in future
    val taxPerUnitInPaisa: Long = 0
    val pricePerUnitInPaisa: Long = 0

    val totalTaxInPaisa: Long = 0 // totalUnits * product.taxPerUnitInPaisa
    val totalPriceWithoutTaxInPaisa: Long = 0 // totalUnits * product.pricePerUnitInPaisa

    // This cart item will ALWAYS belong to a cart
    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "product_order_id")
    var productOrder: ProductOrder? = null;

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
