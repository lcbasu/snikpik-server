package com.server.shop.entities

import com.server.common.entities.Auditable
import javax.persistence.*

@Entity
class ProductPolicy : Auditable() {
    @EmbeddedId
    var id: ProductPolicyKey? = null

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @MapsId("policy_id")
    @JoinColumn(name = "policy_id")
    var policy: Policy? = null;

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @MapsId("product_id")
    @JoinColumn(name = "product_id")
    var product: ProductV3? = null;

}
