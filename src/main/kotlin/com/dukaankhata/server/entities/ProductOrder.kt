package com.dukaankhata.server.entities

import com.dukaankhata.server.enums.ProductOrderStatus
import javax.persistence.*

@Entity
class ProductOrder : Auditable() {

    @Id
    @Column(unique = true)
    var id: String = ""

    // Copying it here even thought we have discount ID so that
    // totalPricePayableInPaisa calculation is faster
    var discountInPaisa: Long = 0
    var deliveryChargeInPaisa: Long = 0
    var totalTaxInPaisa: Long = 0
    var totalPriceWithoutTaxInPaisa: Long = 0
    var totalPricePayableInPaisa: Long = 0 // (totalPriceWithoutTaxInPaisa + taxInPaisa + deliveryChargeInPaisa) - discountInPaisa

    // All orders starts from DRAFT
    @Enumerated(EnumType.STRING)
    var orderStatus: ProductOrderStatus = ProductOrderStatus.DRAFT

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    var address: Address? = null;

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
