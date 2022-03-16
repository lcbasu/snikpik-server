package com.server.shop.entities

import com.server.common.entities.Auditable
import javax.persistence.*

@Entity(name = "product_variant_category_v3")
class ProductVariantCategoryV3 : Auditable() {
    @EmbeddedId
    var id: ProductVariantCategoryV3Key? = null

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @MapsId("product_variant_id")
    @JoinColumn(name = "product_variant_id")
    var productVariant: ProductVariantV3? = null;

}
