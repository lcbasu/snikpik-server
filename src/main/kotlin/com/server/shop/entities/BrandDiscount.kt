package com.server.shop.entities

import com.server.common.entities.Auditable
import javax.persistence.*

@Entity
class BrandDiscount : Auditable() {
    @EmbeddedId
    var id: BrandDiscountKey? = null

    var totalUsedUnit: Long = 0
    var totalAvailableUnit: Long = -1 // -1 means available unlimited times
    var totalAvailableUnitPerUser: Long = -1 // -1 means unlimited use by user

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @MapsId("brand_id")
    @JoinColumn(name = "brand_id")
    var brand: Brand? = null;

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @MapsId("discount_id")
    @JoinColumn(name = "discount_id")
    var discount: DiscountV3? = null;
}
