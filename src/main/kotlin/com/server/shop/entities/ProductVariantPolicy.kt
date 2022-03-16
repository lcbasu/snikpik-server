package com.server.shop.entities

import com.server.common.entities.Auditable
import javax.persistence.*

@Entity
class ProductVariantPolicy : Auditable() {
    @EmbeddedId
    var id: ProductVariantPolicyKey? = null

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @MapsId("policy_id")
    @JoinColumn(name = "policy_id")
    var policy: Policy? = null;

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @MapsId("product_variant_id")
    @JoinColumn(name = "product_variant_id")
    var productVariant: ProductVariantV3? = null;

}
