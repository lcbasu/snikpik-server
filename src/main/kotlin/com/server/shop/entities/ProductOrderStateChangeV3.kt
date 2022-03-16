package com.server.shop.entities

import com.server.common.entities.Auditable
import com.server.common.entities.User
import com.server.common.utils.DateUtils
import com.server.shop.enums.ProductOrderStatusV3
import java.time.LocalDateTime
import javax.persistence.*

@Entity(name = "product_order_state_change_v3")
class ProductOrderStateChangeV3 : Auditable() {

    @Id
    @Column(unique = true)
    var id: String = ""

    @Enumerated(EnumType.STRING)
    var fromProductOrderStatus: ProductOrderStatusV3 = ProductOrderStatusV3.DRAFT

    @Enumerated(EnumType.STRING)
    var toProductOrderStatus: ProductOrderStatusV3 = ProductOrderStatusV3.ADDRESS_ADDED

    var stateChangeAt: LocalDateTime = DateUtils.dateTimeNow()

    var productOrderStateChangeData: String = ""

    // This cart item will ALWAYS belong to a cart
    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "product_order_id")
    var productOrder: ProductOrderV3? = null;

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "added_by_user_id")
    var addedBy: UserV3? = null;
}
