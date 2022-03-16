package com.server.shop.entities

import com.server.common.entities.Auditable
import javax.persistence.*

@Entity(name = "product_discount_v3")
class ProductDiscountV3 : Auditable() {
    @EmbeddedId
    var id: ProductDiscountKeyV3? = null

    var name: String? = null

    var totalUsedUnit: Long = 0
    var totalAvailableUnit: Long = -1 // -1 means available unlimited times
    var totalAvailableUnitPerUser: Long = -1 // -1 means unlimited use by user

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @MapsId("product_id")
    @JoinColumn(name = "product_id")
    var product: ProductV3? = null;

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @MapsId("discount_id")
    @JoinColumn(name = "discount_id")
    var discount: DiscountV3? = null;
}
