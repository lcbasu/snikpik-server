package com.dukaankhata.server.entities

import com.dukaankhata.server.enums.OrderPaymentMode
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

    // Whether by Seller or by Customer, we keep only one update object at any time
    var productOrderStateBeforeUpdate: String = ""
//    var productOrderUpdateBySeller: String = ""
//    var productOrderUpdateByCustomer: String = ""

    // All orders starts from DRAFT
    @Enumerated(EnumType.STRING)
    var orderStatus: ProductOrderStatus = ProductOrderStatus.DRAFT

    @Enumerated(EnumType.STRING)
    var paymentMode: OrderPaymentMode = OrderPaymentMode.NONE

    // The ProductOrderPayment ID which was successful for this product Order
    var successPaymentId: String? = ""

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    var address: Address? = null;

    // Applied on the entire order no matter what is the selling price of each individual ProductVariant
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
