package com.server.dk.entities

import com.server.dk.enums.ProductOrderStatus
import com.server.common.utils.DateUtils
import java.time.LocalDateTime
import javax.persistence.*

@Entity
class ProductOrderStateChange : Auditable() {

    @Id
    @Column(unique = true)
    var id: String = ""

    @Enumerated(EnumType.STRING)
    var productOrderStatus: ProductOrderStatus = ProductOrderStatus.DRAFT

    var stateChangeAt: LocalDateTime = DateUtils.dateTimeNow()

    var productOrderStateChangeData: String = ""

    // This cart item will ALWAYS belong to a cart
    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "product_order_id")
    var productOrder: ProductOrder? = null;

    // Keeping Company reference in all the models
    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    var company: Company? = null;

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "added_by_user_id")
    var addedBy: User? = null;
}
