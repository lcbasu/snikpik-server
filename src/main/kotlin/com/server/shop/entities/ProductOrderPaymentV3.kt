package com.server.shop.entities

import com.server.common.entities.Auditable
import com.server.common.entities.User
import com.server.shop.enums.OrderPaymentModeV3
import com.server.shop.enums.OrderPaymentStatusV3
import javax.persistence.*

@Entity(name = "product_order_payment_v3")
class ProductOrderPaymentV3 : Auditable() {

    @Id
    @Column(unique = true)
    var id: String = ""

    @Enumerated(EnumType.STRING)
    var paymentMode: OrderPaymentModeV3 = OrderPaymentModeV3.ONLINE

    @Enumerated(EnumType.STRING)
    var paymentStatus: OrderPaymentStatusV3 = OrderPaymentStatusV3.STARTED

    // Actual config for Payment from UPI/ Card / PayTm etc
    var paymentConfig: String = ""

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "product_order_id")
    var productOrder: ProductOrderV3? = null;

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "added_by_user_id")
    var addedBy: User? = null;

}
