package com.dukaankhata.server.entities

import com.dukaankhata.server.enums.ProductOrderStatus
import javax.persistence.*

@Entity
class ProductOrder : Auditable() {

    @Id
    var id: String = ""

    // Copying it here even thought we have discount ID so that
    // totalPricePayableInPaisa calculation is faster
    val discountInPaisa: Long = 0
    val deliveryChargeInPaisa: Long = 0
    val totalTaxInPaisa: Long = 0
    val totalPriceWithoutTaxInPaisa: Long = 0
    val totalPricePayableInPaisa: Long = 0 // (totalPriceWithoutTaxInPaisa + taxInPaisa + deliveryChargeInPaisa) - discountInPaisa

    var deliveryAddress: String = "" // Address object

    // All orders starts from DRAFT
    @Enumerated(EnumType.STRING)
    var productOrderStatus: ProductOrderStatus = ProductOrderStatus.DRAFT

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "discount_id")
    var discount: Discount? = null;

    // Keeping Company reference in all the models
    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    var company: Company? = null;

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "added_by_user_id")
    var addedBy: User? = null;
}
