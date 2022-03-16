package com.server.shop.entities

import com.server.common.entities.Auditable
import javax.persistence.*

@Entity(name = "company_discount_v3")
class CompanyDiscountV3 : Auditable() {
    @EmbeddedId
    var id: CompanyDiscountKeyV3? = null

    var totalUsedUnit: Long = 0
    var totalAvailableUnit: Long = -1 // -1 means available unlimited times
    var totalAvailableUnitPerUser: Long = -1 // -1 means unlimited use by user

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @MapsId("company_id")
    @JoinColumn(name = "company_id")
    var company: CompanyV3? = null;

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @MapsId("discount_id")
    @JoinColumn(name = "discount_id")
    var discount: DiscountV3? = null;
}
