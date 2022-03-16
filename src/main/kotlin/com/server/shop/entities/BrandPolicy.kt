package com.server.shop.entities

import com.server.common.entities.Auditable
import javax.persistence.*

@Entity
class BrandPolicy : Auditable() {
    @EmbeddedId
    var id: BrandPolicyKey? = null

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @MapsId("policy_id")
    @JoinColumn(name = "policy_id")
    var policy: Policy? = null;

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @MapsId("brand_id")
    @JoinColumn(name = "brand_id")
    var brand: Brand? = null;

}
