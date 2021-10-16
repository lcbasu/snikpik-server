package com.server.dk.entities

import com.server.dk.enums.OrderPaymentMode
import com.server.dk.enums.OrderPaymentStatus
import javax.persistence.*

@Entity
class ProductOrderPayment : Auditable() {

    @Id
    @Column(unique = true)
    var id: String = ""

    @Enumerated(EnumType.STRING)
    var paymentMode: OrderPaymentMode = OrderPaymentMode.ONLINE

    @Enumerated(EnumType.STRING)
    var paymentStatus: OrderPaymentStatus = OrderPaymentStatus.STARTED

    // Actual config for Payment from UPI/ Card / PayTm etc
    var paymentConfig: String = ""

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "product_order_id")
    var productOrder: ProductOrder? = null;

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    var company: Company? = null;

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "added_by_user_id")
    var addedBy: User? = null;

}
