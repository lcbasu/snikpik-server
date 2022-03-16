package com.server.shop.entities

import com.server.common.entities.Auditable
import javax.persistence.*

@Entity
class ProductVariantVertical : Auditable() {
    @EmbeddedId
    var id: ProductVariantVerticalKey? = null

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @MapsId("product_variant_id")
    @JoinColumn(name = "product_variant_id")
    var productVariant: ProductVariantV3? = null;

}
