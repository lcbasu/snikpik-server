package com.server.shop.entities

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
class ProductVariantDiscountKeyV3: Serializable {

    @Column(name = "product_variant_id")
    var productVariantId: String = ""

    @Column(name = "discount_id")
    var discountId: String = ""
}
