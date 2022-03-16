package com.server.shop.entities

import com.server.common.entities.Auditable
import javax.persistence.*

@Entity(name = "product_category_v3")
class ProductCategoryV3Entity : Auditable() {
    @EmbeddedId
    var id: ProductCategoryV3Key? = null

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @MapsId("product_id")
    @JoinColumn(name = "product_id")
    var product: ProductV3? = null;

}
